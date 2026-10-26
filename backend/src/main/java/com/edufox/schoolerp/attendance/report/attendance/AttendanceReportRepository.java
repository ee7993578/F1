package com.edufox.schoolerp.attendance.report.attendance;
import com.edufox.schoolerp.attendance.work.student_attendance.*;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AttendanceReportRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap(String schoolId, String staffUid, String requestedSession) {
        String session = text(requestedSession).isBlank() ? defaultSession(schoolId) : requestedSession;
        List<Map<String, Object>> sections = authorizedSections(schoolId, staffUid, session);
        Map<Integer, Map<String, Object>> classes = new TreeMap<>();
        for (Map<String, Object> section : sections) {
            int classId = integer(section.get("classId"));
            if (classId <= 0 || classes.containsKey(classId)) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", classId);
            row.put("classId", classId);
            row.put("label", text(section.get("className")));
            row.put("className", text(section.get("className")));
            classes.put(classId, row);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sessions", sessions(schoolId));
        result.put("session", session);
        result.put("attendanceType", attendanceType(schoolId));
        Map<Integer, Map<String, Object>> percentageClasses = new TreeMap<>();
        for (Map<String, Object> section : sections) {
            int classId = integer(section.get("classId"));
            if (classId <= 0 || percentageClasses.containsKey(classId)) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("classId", classId);
            row.put("label", text(section.get("className")));
            percentageClasses.put(classId, row);
        }
        result.put("percentageClasses", new ArrayList<>(percentageClasses.values()));
        result.put("percentageSections", sections);
        result.put("statusClasses", new ArrayList<>(classes.values()));
        // The legacy status page binds this control to section ids even though
        // the ASPX control is named ddlclass. Keep the old values/labels here.
        result.put("statusSections", sections);
        result.put("totalClasses", sections);
        return result;
    }

    /** Direct SQL translation of report_percentage_monthattendance_singleclass and report_percentage_daywiseattendance. */
    public Map<String, Object> percentageReport(String schoolId, String staffUid, String session,
                                                List<Integer> classIds, List<Integer> sectionIds,
                                                String fromPer, String toPer, String fromDate, String toDate) {
        List<Map<String, Object>> allowed = authorizedSections(schoolId, staffUid, session);
        Set<Integer> allowedSections = allowed.stream().map(x -> integer(x.get("sectionId"))).filter(x -> x > 0).collect(Collectors.toSet());
        Set<Integer> allowedClasses = allowed.stream().map(x -> integer(x.get("classId"))).filter(x -> x > 0).collect(Collectors.toSet());
        List<Integer> selectedClasses = (classIds == null ? List.<Integer>of() : classIds).stream().filter(allowedClasses::contains).distinct().toList();
        if (selectedClasses.isEmpty()) selectedClasses = allowedClasses.stream().sorted().toList();
        Set<Integer> selectedClassSet = new LinkedHashSet<>(selectedClasses);
        List<Integer> selectedSections = (sectionIds == null ? List.<Integer>of() : sectionIds).stream()
            .filter(allowedSections::contains).filter(section -> allowed.stream().anyMatch(x -> integer(x.get("sectionId")) == section && selectedClassSet.contains(integer(x.get("classId")))))
            .distinct().toList();
        if (selectedSections.isEmpty()) selectedSections = allowed.stream().filter(x -> selectedClassSet.contains(integer(x.get("classId"))))
            .map(x -> integer(x.get("sectionId"))).filter(x -> x > 0).distinct().toList();
        if (selectedSections.isEmpty()) return reportWithColumns("percentage", List.of(), List.of());

        return attendanceType(schoolId) == 2
            ? dayPercentageReport(schoolId, session, selectedClasses, selectedSections, fromPer, toPer, fromDate, toDate)
            : monthPercentageReport(schoolId, session, selectedClasses, selectedSections, fromPer, toPer);
    }

    private Map<String, Object> monthPercentageReport(String schoolId, String session, List<Integer> classes,
                                                      List<Integer> sections, String fromPer, String toPer) {
        String classIn = placeholders(classes.size()), sectionIn = placeholders(sections.size());
        String percentageClause = text(fromPer).isBlank() ? "" : " WHERE q.totalPer>=? AND q.totalPer<=? ";
        String sql = """
            WITH base AS (
              SELECT s.stu_id,s.registration_no,s.class_id,s.sec_id,s.school_id,
                     LTRIM(RTRIM(COALESCE(i.first_name,'')+' '+COALESCE(i.mid_name,'')+' '+COALESCE(i.last_name,''))) AS studentName,
                     cn.class_name+' ('+c.section+')' AS className,
                     COALESCE((SELECT SUM(TRY_CONVERT(decimal(18,2),m.attendance)) FROM month_attendance m WHERE m.school_id=s.school_id AND m.stu_id=s.stu_id),0) AS attendance,
                     COALESCE((SELECT SUM(TRY_CONVERT(decimal(18,2),m.work_Day)) FROM month_attendance m WHERE m.school_id=s.school_id AND m.stu_id=s.stu_id),0) AS workingDays
              FROM TB_StudentMaster s
              LEFT JOIN student_other_info i ON i.school_id=s.school_id AND i.registration_no=s.registration_no
              LEFT JOIN TB_Class c ON c.school_id=s.school_id AND c.sec_id=s.sec_id
              LEFT JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
              WHERE s.school_id=? AND s.session=? AND s.status='Registered'
                AND s.class_id IN (%s) AND s.sec_id IN (%s)
            ), calc AS (
              SELECT *,CAST(CASE WHEN workingDays=0 THEN 0 ELSE ROUND(attendance*100.0/workingDays,2) END AS decimal(18,2)) AS totalPer FROM base
            )
            SELECT registration_no AS AdmNo,studentName AS StudentName,className AS class,attendance AS Attendance,
                   workingDays AS TotalWorkingDays,CAST(attendance AS varchar(50))+' / '+CAST(workingDays AS varchar(50)) AS Total,
                   totalPer AS per,class_id,sec_id
            FROM calc q%s
            ORDER BY class_id,sec_id,REPLACE(studentName,' ','')
            """.formatted(classIn, sectionIn, percentageClause);
        List<Object> args = new ArrayList<>(List.of(schoolId, session)); args.addAll(classes); args.addAll(sections);
        if (!text(fromPer).isBlank()) { args.add(decimalFilter(fromPer)); args.add(decimalFilter(toPer)); }
        return percentageRows("month", jdbc.queryForList(sql, args.toArray()), List.of(
            column("sno", "S.No."), column("AdmNo", "Adm No"), column("StudentName", "Student Name"),
            column("class", "Class"), column("Attendance", "Attendance"), column("TotalWorkingDays", "Total Working Days"),
            column("Total", "Total"), column("per", "Percentage")));
    }

    private Map<String, Object> dayPercentageReport(String schoolId, String session, List<Integer> classes,
                                                    List<Integer> sections, String fromPer, String toPer,
                                                    String fromDate, String toDate) {
        String classIn = placeholders(classes.size()), sectionIn = placeholders(sections.size());
        String dateClause = text(fromDate).isBlank() ? "" : " AND TRY_CONVERT(date,d.att_date) BETWEEN TRY_CONVERT(date,?) AND TRY_CONVERT(date,?) ";
        String percentageClause = text(fromPer).isBlank() ? "" : " WHERE q.totalPer>=? AND q.totalPer<=? ";
        String sql = """
            WITH base AS (
              SELECT s.stu_id,s.registration_no,s.class_id,s.sec_id,
                     LTRIM(RTRIM(COALESCE(i.first_name,'')+' '+COALESCE(i.mid_name,'')+' '+COALESCE(i.last_name,''))) AS studentName,
                     cn.class_name+' ('+c.section+')' AS className,
                     (SELECT COUNT(*) FROM day_attendance d WHERE d.school_id=s.school_id AND d.stu_id=s.stu_id AND d.att_status='P'%s) AS presentDays,
                     (SELECT COUNT(*) FROM day_attendance d WHERE d.school_id=s.school_id AND d.stu_id=s.stu_id AND d.att_status IN ('A','P','L')%s) AS workDays
              FROM TB_StudentMaster s
              LEFT JOIN student_other_info i ON i.school_id=s.school_id AND i.registration_no=s.registration_no
              LEFT JOIN TB_Class c ON c.school_id=s.school_id AND c.sec_id=s.sec_id
              LEFT JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
              WHERE s.school_id=? AND s.session=? AND s.status='Registered'
                AND s.class_id IN (%s) AND s.sec_id IN (%s)
            ), calc AS (
              SELECT *,CAST(CASE WHEN presentDays=0 OR workDays=0 THEN 0 ELSE ROUND(presentDays*100.0/workDays,2) END AS decimal(18,2)) AS totalPer FROM base
            )
            SELECT stu_id,registration_no, className AS Class,studentName AS Name,
                   CAST(CASE WHEN totalPer=0 THEN 0 ELSE totalPer END AS varchar(70))+'%%' AS Percentage,class_id,sec_id
            FROM calc q%s
            ORDER BY class_id,sec_id,REPLACE(studentName,' ','')
            """.formatted(dateClause, dateClause, classIn, sectionIn, percentageClause);
        List<Object> args = new ArrayList<>();
        if (!text(fromDate).isBlank()) { args.add(fromDate); args.add(toDate); args.add(fromDate); args.add(toDate); }
        args.add(schoolId); args.add(session); args.addAll(classes); args.addAll(sections);
        if (!text(fromPer).isBlank()) { args.add(decimalFilter(fromPer)); args.add(decimalFilter(toPer)); }
        return percentageRows("day", jdbc.queryForList(sql, args.toArray()), List.of(
            column("sno", "S.No."), column("registration_no", "Registration No"), column("Class", "Class"),
            column("Name", "Name"), column("Percentage", "Percentage")));
    }

    private Map<String, Object> percentageRows(String mode, List<Map<String, Object>> raw, List<Map<String, Object>> columns) {
        List<Map<String, Object>> rows = new ArrayList<>(); int serial = 1;
        for (Map<String, Object> source : raw) { Map<String, Object> row = new LinkedHashMap<>(source); row.put("sno", serial++); rows.add(row); }
        return reportWithColumns(mode, columns, rows);
    }

    private BigDecimal decimalFilter(String value) {
        try { return new BigDecimal(text(value).isBlank() ? "0" : text(value)); }
        catch (Exception ignored) { throw new IllegalArgumentException("Percentage must be numeric"); }
    }

    public Map<String, Object> statusReport(String schoolId, String staffUid, String session, int sectionId, String requestedDate) {
        List<Map<String, Object>> allowed = authorizedSections(schoolId, staffUid, session);
        Set<Integer> allowedSections = allowed.stream().map(x -> integer(x.get("sectionId"))).collect(Collectors.toSet());
        if (allowedSections.isEmpty()) return reportWithColumns("month", List.of(), List.of());
        if (sectionId <= 0 || !allowedSections.contains(sectionId)) return reportWithColumns("month", List.of(), List.of());
        List<Integer> sections = List.of(sectionId);
        String in = placeholders(sections.size());
        String date = text(requestedDate);
        boolean dayWise = attendanceType(schoolId) == 2 && !date.isBlank();
        if (attendanceType(schoolId) == 2 && date.isBlank()) {
            throw new IllegalArgumentException("Attendance date is required");
        }

        Map<Integer, String> teachers = teachers(schoolId, session);
        if (!dayWise) {
            String sql = """
                SELECT c.sec_id AS sectionId,c.class_id AS classId,cn.class_name+' ('+c.section+')' AS className,
                       m.month_id AS monthId,m.month_name AS monthName,COUNT(ma.stu_id) AS studentCount
                FROM TB_Class c
                JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
                JOIN TB_StudentMaster s ON s.school_id=c.school_id AND s.sec_id=c.sec_id AND s.session=? AND LOWER(s.status)='registered'
                CROSS JOIN tb_month m
                LEFT JOIN month_attendance ma ON ma.school_id=s.school_id AND ma.session=s.session AND ma.stu_id=s.stu_id AND ma.month_id=m.month_id
                WHERE c.school_id=? AND c.sec_id IN (%s)
                GROUP BY c.sec_id,c.class_id,c.section,cn.class_name,m.month_id,m.month_name,m.id
                ORDER BY c.class_id,c.sec_id,m.id
                """.formatted(in);
            List<Object> args = new ArrayList<>(List.of(session, schoolId));
            args.addAll(sections);
            List<Map<String, Object>> raw = jdbc.queryForList(sql, args.toArray());
            return monthStatus(raw, teachers);
        }

        String sql = """
            SELECT c.sec_id AS sectionId,c.class_id AS classId,cn.class_name+' ('+c.section+')' AS className,
                   COALESCE(da.att_status,'No Status') AS studentStatus,COUNT(*) AS statusCount
            FROM TB_Class c
            JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            JOIN TB_StudentMaster s ON s.school_id=c.school_id AND s.sec_id=c.sec_id AND s.session=? AND LOWER(s.status)='registered'
            LEFT JOIN day_attendance da ON da.school_id=s.school_id AND da.session=s.session AND da.stu_id=s.stu_id
                AND TRY_CONVERT(date,da.att_date)=TRY_CONVERT(date,?)
            WHERE c.school_id=? AND c.sec_id IN (%s)
            GROUP BY c.sec_id,c.class_id,c.section,cn.class_name,COALESCE(da.att_status,'No Status')
            ORDER BY c.class_id,c.sec_id,COALESCE(da.att_status,'No Status')
            """.formatted(in);
        List<Object> args = new ArrayList<>(List.of(session, date, schoolId));
        args.addAll(sections);
        List<Map<String, Object>> raw = jdbc.queryForList(sql, args.toArray());
        return dayStatus(raw, teachers);
    }

    public Map<String, Object> totalReport(String schoolId, String staffUid, String session, int sectionId) {
        List<Map<String, Object>> allowed = authorizedSections(schoolId, staffUid, session);
        Set<Integer> allowedIds = allowed.stream().map(x -> integer(x.get("sectionId"))).collect(Collectors.toSet());
        if (sectionId <= 0 || !allowedIds.contains(sectionId)) return report(List.of(), List.of());
        Integer classId = jdbc.query("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", (rs, n) -> rs.getInt(1), schoolId, sectionId)
            .stream().findFirst().orElse(null);
        if (classId == null) return report(List.of(), List.of());

        List<Map<String, Object>> students = jdbc.queryForList("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
                   LTRIM(RTRIM(COALESCE(i.first_name,'')+' '+COALESCE(i.mid_name,'')+' '+COALESCE(i.last_name,''))) AS studentName
            FROM TB_StudentMaster s
            LEFT JOIN student_other_info i ON i.school_id=s.school_id AND i.registration_no=s.registration_no
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered'
            ORDER BY REPLACE(COALESCE(i.first_name,''),' ',''),
                     CASE WHEN REPLACE(COALESCE(i.mid_name,''),' ','')='' THEN REPLACE(COALESCE(i.last_name,''),' ','') ELSE REPLACE(COALESCE(i.mid_name,''),' ','') END,
                     s.registration_no
            """, schoolId, session, sectionId);
        if (students.isEmpty()) return report(List.of(), List.of());

        String schoolName = firstString("SELECT TOP 1 schoolname FROM schoolmaster WHERE school_id=?", schoolId);
        String className = firstString("SELECT TOP 1 cn.class_name+' ('+c.section+')' FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id WHERE c.school_id=? AND c.sec_id=?", schoolId, sectionId);
        int type = attendanceType(schoolId);
        List<TermDef> terms = terms(schoolId, session, classId);
        List<Map<String, Object>> resultRows = type == 2
            ? buildDayWiseTotal(schoolId, session, students, terms)
            : buildMonthWiseTotal(schoolId, session, students, terms);
        List<Map<String, Object>> columns = type == 2
            ? dayTotalColumns(terms)
            : monthTotalColumns(terms);

        Map<String, Object> result = report(columns.stream().map(x -> text(x.get("key"))).toList(), resultRows);
        result.put("columns", columns);
        result.put("schoolName", schoolName == null ? "" : schoolName);
        result.put("className", className == null ? "" : className);
        result.put("session", session);
        result.put("attendanceType", type);
        result.put("terms", terms.stream().map(TermDef::asMap).toList());
        return result;
    }

    private Map<String, Object> monthStatus(List<Map<String, Object>> raw, Map<Integer, String> teachers) {
        LinkedHashMap<Integer, Map<String, Object>> rows = new LinkedHashMap<>();
        LinkedHashMap<Integer, String> monthNames = new LinkedHashMap<>();
        for (Map<String, Object> item : raw) {
            int section = integer(item.get("sectionId"));
            Map<String, Object> row = rows.computeIfAbsent(section, key -> {
                Map<String, Object> value = new LinkedHashMap<>();
                value.put("className", item.get("className"));
                value.put("classTeacher", teachers.getOrDefault(section, ""));
                return value;
            });
            int month = integer(item.get("monthId"));
            String key = "month_" + month;
            monthNames.putIfAbsent(month, text(item.get("monthName")));
            row.put(key, integer(item.get("studentCount")));
        }
        List<Map<String, Object>> columns = new ArrayList<>();
        columns.add(column("className", "Class"));
        columns.add(column("classTeacher", "Class Teacher"));
        monthNames.forEach((id, name) -> columns.add(column("month_" + id, name)));
        return reportWithColumns("month", columns, new ArrayList<>(rows.values()));
    }

    private Map<String, Object> dayStatus(List<Map<String, Object>> raw, Map<Integer, String> teachers) {
        LinkedHashMap<Integer, Map<String, Object>> rows = new LinkedHashMap<>();
        Set<String> statuses = new LinkedHashSet<>();
        for (Map<String, Object> item : raw) {
            int section = integer(item.get("sectionId"));
            String status = text(item.get("studentStatus"));
            if (status.isBlank()) status = "No Status";
            statuses.add(status);
            Map<String, Object> row = rows.computeIfAbsent(section, key -> {
                Map<String, Object> value = new LinkedHashMap<>();
                value.put("className", item.get("className"));
                value.put("classTeacher", teachers.getOrDefault(section, ""));
                value.put("total", 0);
                return value;
            });
            int count = integer(item.get("statusCount"));
            row.put("total", integer(row.get("total")) + count);
            row.put(statusKey(status), count);
        }
        Map<String, Integer> priorities = jdbc.queryForList("SELECT att_Status,priority FROM Attendance_Status")
            .stream().collect(Collectors.toMap(x -> text(x.get("att_Status")), x -> integer(x.get("priority")), (a, b) -> a));
        List<String> ordered = statuses.stream().sorted(Comparator.<String>comparingInt(x -> priorities.getOrDefault(x, 100)).thenComparing(String::compareToIgnoreCase)).toList();
        List<Map<String, Object>> output = new ArrayList<>(rows.values());
        Map<String, Object> total = new LinkedHashMap<>();
        total.put("className", "Total");
        total.put("classTeacher", "");
        total.put("total", output.stream().mapToInt(x -> integer(x.get("total"))).sum());
        for (String status : ordered) total.put(statusKey(status), output.stream().mapToInt(x -> integer(x.get(statusKey(status)))).sum());
        output.add(total);

        List<Map<String, Object>> columns = new ArrayList<>();
        columns.add(column("className", "Class"));
        columns.add(column("classTeacher", "Class Teacher"));
        columns.add(column("total", "Total"));
        for (String status : ordered) columns.add(column(statusKey(status), status));
        return reportWithColumns("day", columns, output);
    }

    private List<Map<String, Object>> buildMonthWiseTotal(String schoolId, String session, List<Map<String, Object>> students, List<TermDef> terms) {
        Map<Integer, Map<Integer, Map<String, String>>> attendance = new LinkedHashMap<>();
        List<Map<String, Object>> raw = jdbc.queryForList("SELECT stu_id,term,month_id,attendance,work_Day FROM month_attendance WHERE school_id=? AND session=?", schoolId, session);
        for (Map<String, Object> item : raw) {
            int student = integer(item.get("stu_id"));
            attendance.computeIfAbsent(student, x -> new LinkedHashMap<>()).computeIfAbsent(integer(item.get("month_id")), x -> new LinkedHashMap<>())
                .put("attendance_" + integer(item.get("term")), text(item.get("attendance")));
            attendance.get(student).get(integer(item.get("month_id"))).put("work_" + integer(item.get("term")), text(item.get("work_Day")));
        }
        List<Map<String, Object>> out = new ArrayList<>();
        int sno = 1;
        for (Map<String, Object> student : students) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("sno", sno++);
            row.put("registrationNo", student.get("registrationNo"));
            row.put("studentName", student.get("studentName"));
            int yearlyAttendance = 0, yearlyWork = 0;
            for (TermDef term : terms) {
                int termAttendance = 0, termWork = 0;
                for (MonthDef month : term.months()) {
                    Map<String, String> value = attendance.getOrDefault(integer(student.get("studentId")), Map.of()).getOrDefault(month.number(), Map.of());
                    String att = value.getOrDefault("attendance_" + term.id(), "");
                    String work = value.getOrDefault("work_" + term.id(), "");
                    row.put(monthKey("att", term, month), att);
                    row.put(monthKey("work", term, month), work);
                    row.put(monthKey("pct", term, month), percentage(att, work));
                    termAttendance += number(att);
                    termWork += number(work);
                }
                yearlyAttendance += termAttendance;
                yearlyWork += termWork;
                row.put(termKey("att", term), numberOrBlank(termAttendance, termWork));
                row.put(termKey("work", term), termWork == 0 && termAttendance == 0 ? "" : String.valueOf(termWork));
                row.put(termKey("pct", term), percentage(String.valueOf(termAttendance), String.valueOf(termWork)));
            }
            row.put("yearAttendance", numberOrBlank(yearlyAttendance, yearlyWork));
            row.put("yearWork", yearlyWork == 0 && yearlyAttendance == 0 ? "" : String.valueOf(yearlyWork));
            row.put("yearPct", percentage(String.valueOf(yearlyAttendance), String.valueOf(yearlyWork)));
            out.add(row);
        }
        return out;
    }

    private List<Map<String, Object>> buildDayWiseTotal(String schoolId, String session, List<Map<String, Object>> students, List<TermDef> terms) {
        Set<Integer> studentIds = students.stream().map(x -> integer(x.get("studentId"))).collect(Collectors.toSet());
        Map<Integer, Map<Integer, int[]>> counts = new LinkedHashMap<>();
        List<Map<String, Object>> raw = jdbc.queryForList("SELECT stu_id,att_date,att_status FROM day_attendance WHERE school_id=? AND session=?", schoolId, session);
        for (Map<String, Object> item : raw) {
            int student = integer(item.get("stu_id"));
            if (!studentIds.contains(student)) continue;
            LocalDate date = toDate(item.get("att_date"));
            if (date == null) continue;
            int[] value = counts.computeIfAbsent(student, x -> new LinkedHashMap<>()).computeIfAbsent(date.getMonthValue(), x -> new int[2]);
            String status = text(item.get("att_status"));
            if ("P".equalsIgnoreCase(status)) value[0]++;
            if (List.of("A", "P", "L").contains(status.toUpperCase(Locale.ROOT))) value[1]++;
        }
        List<Map<String, Object>> out = new ArrayList<>();
        int sno = 1;
        for (Map<String, Object> student : students) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("sno", sno++);
            row.put("registrationNo", student.get("registrationNo"));
            row.put("studentName", student.get("studentName"));
            int yearlyPresent = 0, yearlyWork = 0;
            for (TermDef term : terms) {
                int termPresent = 0, termWork = 0;
                for (MonthDef month : term.months()) {
                    int[] value = counts.getOrDefault(integer(student.get("studentId")), Map.of()).getOrDefault(month.number(), new int[2]);
                    termPresent += value[0]; termWork += value[1]; yearlyPresent += value[0]; yearlyWork += value[1];
                    row.put(dayMonthKey(term, month), value[0] + "/" + value[1]);
                }
                row.put(dayTermKey(term), termPresent + "/" + termWork + " [" + percentage(String.valueOf(termPresent), String.valueOf(termWork)) + " %]");
            }
            row.put("yearAttendance", yearlyPresent + "/" + yearlyWork);
            row.put("yearPct", percentage(String.valueOf(yearlyPresent), String.valueOf(yearlyWork)));
            out.add(row);
        }
        return out;
    }

    private List<Map<String, Object>> monthTotalColumns(List<TermDef> terms) {
        List<Map<String, Object>> columns = baseTotalColumns();
        for (TermDef term : terms) {
            for (MonthDef month : term.months()) {
                columns.add(column(monthKey("att", term, month), month.name() + " Attendance"));
                columns.add(column(monthKey("work", term, month), month.name() + " Workday"));
                columns.add(column(monthKey("pct", term, month), month.name() + " %"));
            }
            columns.add(column(termKey("att", term), "Term " + term.order() + " Total"));
            columns.add(column(termKey("work", term), "Term " + term.order() + " Workday"));
            columns.add(column(termKey("pct", term), "Term " + term.order() + " %"));
        }
        columns.add(column("yearAttendance", "Attendance"));
        columns.add(column("yearWork", "Workday"));
        columns.add(column("yearPct", "Yearly Attendance %"));
        return columns;
    }

    private List<Map<String, Object>> dayTotalColumns(List<TermDef> terms) {
        List<Map<String, Object>> columns = baseTotalColumns();
        for (TermDef term : terms) {
            for (MonthDef month : term.months()) columns.add(column(dayMonthKey(term, month), month.name()));
            columns.add(column(dayTermKey(term), "Term " + term.order() + " Attendance Total"));
        }
        columns.add(column("yearAttendance", "Yearly Attendance"));
        columns.add(column("yearPct", "Yearly Attendance %"));
        return columns;
    }

    private List<Map<String, Object>> baseTotalColumns() {
        return new ArrayList<>(List.of(column("sno", "S.No."), column("registrationNo", "Registration No"), column("studentName", "Name")));
    }

    private List<TermDef> terms(String schoolId, String session, int classId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT DISTINCT e.term_id AS termId,COALESCE(t.term_name,CAST(e.term_id AS varchar(50))) AS termName
            FROM exam_set e
            JOIN assign_exam_set a ON a.exam_set_id=e.exam_set_id AND a.school_id=? AND a.class_id=?
            LEFT JOIN term t ON t.term_id=e.term_id
            WHERE e.school_id=? AND a.from_year<=? AND (a.to_year='0' OR a.to_year>=?)
            ORDER BY e.term_id
            """, schoolId, classId, schoolId, yearStart(session), yearEnd(session));
        if (rows.isEmpty()) rows = jdbc.queryForList("SELECT DISTINCT term AS termId,CAST(term AS varchar(50)) AS termName FROM session_term WHERE school_id=? AND session=? AND class_id=? ORDER BY term", schoolId, session, classId);
        List<TermDef> out = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            int id = integer(rows.get(i).get("termId"));
            List<Map<String, Object>> monthRows = jdbc.queryForList("SELECT startdate,enddate FROM session_term WHERE school_id=? AND session=? AND class_id=? AND term=? ORDER BY startdate,s_no", schoolId, session, classId, id);
            List<MonthDef> months = new ArrayList<>();
            for (Map<String, Object> bound : monthRows) {
                LocalDate start = toDate(bound.get("startdate"));
                LocalDate end = toDate(bound.get("enddate"));
                if (start == null || end == null) continue;
                for (LocalDate date = start.withDayOfMonth(1); date.isBefore(end); date = date.plusMonths(1)) {
                    months.add(new MonthDef(date.getMonthValue(), date.getYear(), date.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH)));
                }
            }
            out.add(new TermDef(id, i + 1, text(rows.get(i).get("termName")), months));
        }
        return out;
    }

    private List<Map<String, Object>> authorizedSections(String schoolId, String staffUid, String session) {
        String faculty = firstString("SELECT TOP 1 CAST(f_id AS varchar(50)) FROM TB_FacultyMaster WHERE school_id=? AND staff_u_id=?", schoolId, staffUid);
        if (faculty == null) faculty = "";
        int unrestricted = jdbc.queryForObject("SELECT COUNT(*) FROM desig_mapping WHERE school_id=? AND (Staff_U_id=? OR CAST(f_id AS varchar(50))=?) AND Desg_id IN (28,39)", Integer.class, schoolId, staffUid, faculty);
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,
                   cn.class_name AS className,cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c
            JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE c.school_id=? AND EXISTS (
                SELECT 1 FROM classsessionmapping m WHERE m.school_id=c.school_id AND m.sec_id=c.sec_id AND m.status=1
                  AND m.from_year<=? AND (m.to_year='0' OR (m.to_year<>'0' AND m.to_year>=?)))
            """);
        List<Object> args = new ArrayList<>(List.of(schoolId, yearStart(session), yearEnd(session)));
        if (unrestricted == 0) {
            sql.append(" AND (EXISTS (SELECT 1 FROM Tb_ClassTeacher ct WHERE ct.school_id=c.school_id AND ct.section_id=c.sec_id AND ct.session=? AND (CAST(ct.staff_u_id AS varchar(50))=? OR CAST(ct.f_id AS varchar(50))=?))");
            args.add(session); args.add(staffUid); args.add(faculty);
            sql.append(" OR EXISTS (SELECT 1 FROM roles_permission_efox rp CROSS APPLY STRING_SPLIT(COALESCE(rp.class_id,''),',') x WHERE rp.school_id=c.school_id AND rp.Status=1 AND rp.Desi_Id IN (SELECT dm.Desg_id FROM desig_mapping dm WHERE dm.school_id=? AND (CAST(dm.Staff_U_id AS varchar(50))=? OR CAST(dm.f_id AS varchar(50))=?)) AND TRY_CONVERT(int,x.value)=c.class_id))");
            args.add(schoolId); args.add(staffUid); args.add(faculty);
            sql.append(")");
        }
        sql.append(" ORDER BY c.class_id,c.sec_id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    private Map<Integer, String> teachers(String schoolId, String session) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            WITH ranked AS (
              SELECT ct.section_id,
                     LTRIM(RTRIM(COALESCE(f.first_name,'')+' '+COALESCE(f.mid_name,'')+' '+COALESCE(f.last_name,''))) AS teacher,
                     ROW_NUMBER() OVER(PARTITION BY ct.section_id ORDER BY ct.ct_id) AS rn
              FROM Tb_ClassTeacher ct
              LEFT JOIN TB_FacultyMaster f ON f.school_id=ct.school_id AND f.f_id=ct.f_id
              WHERE ct.school_id=? AND ct.session=?
            ) SELECT section_id,teacher FROM ranked WHERE rn=1
            """, schoolId, session);
        Map<Integer, String> result = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) result.putIfAbsent(integer(row.get("section_id")), text(row.get("teacher")));
        return result;
    }

    private List<Map<String, Object>> sessions(String schoolId) {
        return jdbc.queryForList("SELECT session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY id DESC", schoolId);
    }

    private String defaultSession(String schoolId) {
        return firstString("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC", schoolId);
    }

    private int attendanceType(String schoolId) {
        Integer value = jdbc.queryForObject("SELECT TOP 1 attendance_type FROM configuration WHERE school_id=?", Integer.class, schoolId);
        return value == null ? 1 : value;
    }

    private Map<String, Object> report(List<String> keys, List<?> rows) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("columns", keys.stream().map(key -> column(key, key)).toList());
        result.put("rows", rows);
        return result;
    }

    private Map<String, Object> reportWithColumns(String mode, List<Map<String, Object>> columns, List<Map<String, Object>> rows) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mode", mode);
        result.put("columns", columns);
        result.put("rows", rows);
        return result;
    }

    private Map<String, Object> column(String key, String label) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("key", key); value.put("label", label);
        return value;
    }

    private static String placeholders(int count) { return String.join(",", java.util.Collections.nCopies(count, "?")); }
    private static String statusKey(String status) { return "status_" + status.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_"); }
    private static String monthKey(String type, TermDef term, MonthDef month) { return type + "_" + term.id() + "_" + month.number() + "_" + month.year(); }
    private static String termKey(String type, TermDef term) { return type + "_term_" + term.id(); }
    private static String dayMonthKey(TermDef term, MonthDef month) { return "day_" + term.id() + "_" + month.number() + "_" + month.year(); }
    private static String dayTermKey(TermDef term) { return "day_term_" + term.id(); }
    private static int integer(Object value) { try { return Integer.parseInt(text(value)); } catch (Exception ignored) { return 0; } }
    private static int number(String value) { try { return new BigDecimal(text(value).isBlank() ? "0" : text(value)).intValue(); } catch (Exception ignored) { return 0; } }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static String numberOrBlank(int value, int work) { return value == 0 && work == 0 ? "" : String.valueOf(value); }
    private static String percentage(String attendance, String work) {
        int a = number(attendance), w = number(work);
        if (text(attendance).isBlank() && text(work).isBlank()) return "";
        BigDecimal denominator = BigDecimal.valueOf(w == 0 ? 1 : w);
        return BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(100)).divide(denominator, 2, RoundingMode.HALF_UP).toPlainString();
    }
    private static LocalDate toDate(Object value) {
        if (value == null) return null;
        if (value instanceof Timestamp timestamp) return timestamp.toLocalDateTime().toLocalDate();
        if (value instanceof Date date) return date.toLocalDate();
        String raw = text(value);
        if (raw.length() >= 10) {
            try { return LocalDate.parse(raw.substring(0, 10)); } catch (Exception ignored) { }
        }
        return null;
    }
    private static String yearStart(String session) {
        return session == null || session.length() < 4 ? session : session.substring(0, 4);
    }
    private static String yearEnd(String session) {
        return session == null || session.length() < 4 ? session : session.substring(session.length() - 4);
    }
    private String firstString(String sql, Object... args) {
        return jdbc.query(sql, (rs, n) -> rs.getString(1), args).stream().findFirst().orElse(null);
    }

    private record MonthDef(int number, int year, String name) { }
    private record TermDef(int id, int order, String name, List<MonthDef> months) {
        Map<String, Object> asMap() { return Map.of("id", id, "order", order, "name", name, "months", months); }
    }
}
