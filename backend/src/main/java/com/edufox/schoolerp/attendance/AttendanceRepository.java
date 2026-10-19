package com.edufox.schoolerp.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AttendanceRepository {
    private final JdbcTemplate jdbc;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    public Map<String, Object> bootstrap(String schoolId, String staffUid) {
        String session = defaultSession(schoolId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("sessions", sessions(schoolId));
        out.put("session", session);
        out.put("classes", staffClasses(schoolId, staffUid, session));
        out.put("allClasses", allClasses(schoolId));
        out.put("attendanceTypes", attendanceTypes(schoolId));
        out.put("routes", routes(schoolId));
        out.put("today", LocalDate.now().toString());
        return out;
    }

    public String defaultSession(String schoolId) {
        List<String> rows = jdbc.query("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC",
            (rs, n) -> rs.getString(1), schoolId);
        if (!rows.isEmpty()) return rows.get(0);
        rows = jdbc.query("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY id DESC",
            (rs, n) -> rs.getString(1), schoolId);
        return rows.isEmpty() ? "" : rows.get(0);
    }

    public List<Map<String, Object>> sessions(String schoolId) {
        return jdbc.queryForList("SELECT session,status FROM TB_Session WHERE school_id=? ORDER BY id DESC", schoolId);
    }

    public List<Map<String, Object>> attendanceTypes(String schoolId) {
        return jdbc.queryForList("""
            SELECT id,attendance_type AS name
            FROM attendance_type
            WHERE id=(SELECT TOP 1 attendance_type FROM configuration WHERE school_id=?)
            """, schoolId);
    }

    public List<Map<String, Object>> allClasses(String schoolId) {
        return jdbc.queryForList("""
            SELECT id AS classId,class_name AS className,COALESCE(displayClassName,class_name) AS displayClassName
            FROM class_name WHERE school_id=? ORDER BY COALESCE(class_order,id),id
            """, schoolId);
    }

    /** Direct equivalent of BindStaffClasses for the attendance pages. */
    public List<Map<String, Object>> staffClasses(String schoolId, String staffUid, String session) {
        String faculty = firstString("SELECT TOP 1 CAST(f_id AS varchar(50)) FROM TB_FacultyMaster WHERE school_id=? AND staff_u_id=?",
            schoolId, staffUid);
        if (faculty == null) faculty = "";
        boolean unrestricted = jdbc.queryForObject("""
            SELECT COUNT(*) FROM desig_mapping
            WHERE school_id=? AND f_id=? AND Desg_id IN ('28','39')
            """, Integer.class, schoolId, faculty) > 0;

        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,
                   cn.class_name AS className,cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE c.school_id=? AND EXISTS (
                SELECT 1 FROM classsessionmapping m
                WHERE m.school_id=c.school_id AND m.sec_id=c.sec_id AND m.status=1
                  AND m.from_year<=? AND (m.to_year='0' OR (m.to_year<> '0' AND m.to_year>=?)))
            """);
        List<Object> args = new ArrayList<>(List.of(schoolId, yearStart(session), yearEnd(session)));
        if (unrestricted) {
            sql.append(" ORDER BY c.class_id,c.sec_id");
            return jdbc.queryForList(sql.toString(), args.toArray());
        }
        sql.append(" AND (EXISTS (SELECT 1 FROM Tb_ClassTeacher ct WHERE ct.school_id=c.school_id AND ct.section_id=c.sec_id AND ct.session=? AND (ct.f_id=? OR ct.staff_u_id=?))");
        args.add(session); args.add(faculty); args.add(staffUid);
        sql.append(" OR EXISTS (SELECT 1 FROM roles_permission_efox rp CROSS APPLY STRING_SPLIT(COALESCE(rp.class_id,''),',') x WHERE rp.school_id=c.school_id AND rp.Status=1 AND rp.Desi_Id IN (SELECT dm.Desg_id FROM desig_mapping dm WHERE dm.school_id=? AND dm.f_id=?) AND TRY_CONVERT(int,x.value)=c.class_id))");
        args.add(schoolId); args.add(faculty);
        sql.append(") ORDER BY c.class_id,c.sec_id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public List<Map<String, Object>> routes(String schoolId) {
        return jdbc.queryForList("SELECT route_id AS id,route_id AS routeId,route_name AS name,route_name AS routeName FROM Route_Detail WHERE school_id=? ORDER BY COALESCE(Route_order,999),route_name", schoolId);
    }

    public List<Map<String, Object>> dayRows(String schoolId, String session, int sectionId, String date) {
        String d = normalizeDate(date).toString();
        return jdbc.queryForList("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
                   (COALESCE(st.first_name,'')+' '+COALESCE(st.mid_name,'')+' '+COALESCE(st.last_name,'')) AS studentName,
                   COALESCE(m.att_status,'P') AS attendanceStatus,COALESCE(m.att_date,'') AS attendanceDate,
                   COALESCE(m.SourceType,'') AS sourceType
            FROM TB_StudentMaster s
            LEFT JOIN student_other_info st ON st.registration_no=s.registration_no AND st.school_id=s.school_id
            LEFT JOIN day_attendance m ON m.stu_id=s.stu_id AND m.school_id=s.school_id AND m.session=s.session
                AND TRY_CONVERT(date,m.att_date)=TRY_CONVERT(date,?)
            WHERE s.session=? AND s.school_id=? AND LOWER(s.status)='registered' AND s.sec_id=?
            ORDER BY REPLACE(COALESCE(st.first_name,''),' ',''),
                     CASE WHEN REPLACE(COALESCE(st.mid_name,''),' ','')='' THEN REPLACE(COALESCE(st.last_name,''),' ','') ELSE REPLACE(COALESCE(st.mid_name,''),' ','') END,
                     CASE WHEN REPLACE(COALESCE(st.mid_name,''),' ','')='' THEN '' ELSE REPLACE(COALESCE(st.last_name,''),' ','') END
            """, d, session, schoolId, sectionId);
    }

    public void saveDay(String schoolId, String staffUid, String session, int sectionId, String date,
                        List<Map<String, Object>> records) {
        String d = normalizeDate(date).toString();
        for (Map<String, Object> row : records == null ? List.<Map<String, Object>>of() : records) {
            int studentId = integer(row.get("studentId"));
            String status = text(row.get("status"));
            if (studentId <= 0 || !List.of("P","A","L","H","NA").contains(status)) continue;
            Integer exists = jdbc.queryForObject("""
                SELECT COUNT(*) FROM day_attendance WHERE school_id=? AND stu_id=? AND session=? AND TRY_CONVERT(date,att_date)=TRY_CONVERT(date,?)
                """, Integer.class, schoolId, studentId, session, d);
            if (exists != null && exists > 0) {
                jdbc.update("""
                    UPDATE day_attendance SET att_status=?,modified_date=GETDATE(),modified_by=?
                    WHERE school_id=? AND stu_id=? AND session=? AND TRY_CONVERT(date,att_date)=TRY_CONVERT(date,?)
                    """, status, staffUid, schoolId, studentId, session, d);
            } else {
                jdbc.update("""
                    INSERT INTO day_attendance(Stu_id,att_status,att_date,session,created_date,created_by,modified_date,modified_by,school_id)
                    VALUES(?,?,?,?,GETDATE(),?,'','',?)
                    """, studentId, status, d, session, staffUid, schoolId);
            }
        }
        refreshMonthFromDay(schoolId, staffUid, session, sectionId, d);
    }

    /** Direct equivalent of update_monthattendance_from_dayattendance called by insert_day_attendance. */
    private void refreshMonthFromDay(String schoolId, String staffUid, String session, int sectionId, String date) {
        Integer classId = jdbc.queryForObject("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", Integer.class, schoolId, sectionId);
        if (classId == null) return;
        LocalDate day = normalizeDate(date);
        List<Map<String, Object>> counts = jdbc.queryForList("""
            SELECT d.stu_id,
                   SUM(CASE WHEN d.att_status='P' THEN 1 ELSE 0 END) AS presentDays,
                   SUM(CASE WHEN d.att_status IN ('P','A','L') THEN 1 ELSE 0 END) AS workDays,
                   (SELECT TOP 1 st.term FROM session_term st WHERE st.school_id=? AND st.session=? AND st.class_id=?
                    AND CAST(st.startdate AS date)<=CAST(d.att_date AS date) AND CAST(st.enddate AS date)>=CAST(d.att_date AS date)) AS termId
            FROM day_attendance d
            JOIN TB_StudentMaster s ON s.school_id=d.school_id AND s.stu_id=d.stu_id AND s.session=d.session AND s.sec_id=? AND LOWER(s.status)='registered'
            WHERE d.school_id=? AND d.session=? AND MONTH(TRY_CONVERT(date,d.att_date))=? AND YEAR(TRY_CONVERT(date,d.att_date))=?
              AND d.att_status IN ('A','P','L','H','NA')
            GROUP BY d.stu_id,d.att_date
            """, schoolId, session, classId, sectionId, schoolId, session, day.getMonthValue(), day.getYear());
        // The legacy procedure aggregates by student and term. Re-query with that grouping to preserve it.
        counts = jdbc.queryForList("""
            SELECT d.stu_id,
                   SUM(CASE WHEN d.att_status='P' THEN 1 ELSE 0 END) AS presentDays,
                   SUM(CASE WHEN d.att_status IN ('P','A','L') THEN 1 ELSE 0 END) AS workDays,
                   (SELECT TOP 1 st.term FROM session_term st WHERE st.school_id=? AND st.session=? AND st.class_id=?
                    AND CAST(st.startdate AS date)<=CAST(? AS date) AND CAST(st.enddate AS date)>=CAST(? AS date)) AS termId
            FROM day_attendance d
            JOIN TB_StudentMaster s ON s.school_id=d.school_id AND s.stu_id=d.stu_id AND s.session=d.session AND s.sec_id=? AND LOWER(s.status)='registered'
            WHERE d.school_id=? AND d.session=? AND MONTH(TRY_CONVERT(date,d.att_date))=? AND YEAR(TRY_CONVERT(date,d.att_date))=?
              AND d.att_status IN ('A','P','L','H','NA')
            GROUP BY d.stu_id
            """, schoolId, session, classId, date, date, sectionId, schoolId, session, day.getMonthValue(), day.getYear());
        for (Map<String, Object> row : counts) {
            if (row.get("termId") == null) continue;
            int studentId = integer(row.get("stu_id"));
            int month = day.getMonthValue();
            String year = String.valueOf(day.getYear());
            int term = integer(row.get("termId"));
            upsertMonth(schoolId, staffUid, session, studentId, term, month, year,
                text(row.get("presentDays")), text(row.get("workDays")));
        }
    }

    public Map<String, Object> monthOptions(String schoolId, String session, int sectionId, Integer selectedTerm) {
        Integer classId = classId(schoolId, session, sectionId);
        List<Map<String, Object>> terms = classId == null ? List.of() : jdbc.queryForList("""
            SELECT DISTINCT e.term_id AS id,t.term_name AS name
            FROM exam_set e JOIN term t ON t.term_id=e.term_id
            WHERE e.exam_set_id=(SELECT TOP 1 a.exam_set_id FROM assign_exam_set a WHERE a.school_id=? AND a.class_id=?
                AND a.from_year<=? AND (a.to_year='0' OR a.to_year>=?)) AND e.school_id=? ORDER BY e.term_id
            """, schoolId, classId, yearStart(session), yearEnd(session), schoolId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("terms", terms);
        out.put("months", months(schoolId, session, classId, selectedTerm));
        return out;
    }

    public Map<String, Object> monthlyRows(String schoolId, String session, int sectionId, int term, int month, int year) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
                   (COALESCE(st.first_name,'')+' '+COALESCE(st.mid_name,'')+' '+COALESCE(st.last_name,'')) AS studentName,
                   COALESCE(m.attendance,'') AS attendance,COALESCE(m.work_Day,'') AS workDays
            FROM TB_StudentMaster s
            LEFT JOIN student_other_info st ON st.registration_no=s.registration_no AND st.school_id=s.school_id
            LEFT JOIN month_attendance m ON m.stu_id=s.stu_id AND m.school_id=s.school_id AND m.session=s.session
                AND m.term=? AND m.month_id=? AND m.year_of_month=?
            WHERE s.school_id=? AND s.session=? AND LOWER(s.status)='registered' AND s.sec_id=?
            ORDER BY REPLACE(COALESCE(st.first_name,''),' ',''),COALESCE(st.mid_name,''),COALESCE(st.last_name,'')
            """, term, month, String.valueOf(year), schoolId, session, sectionId);
        String work = firstString("""
            SELECT MAX(TRY_CONVERT(int,work_Day)) FROM month_attendance m JOIN TB_StudentMaster s ON s.school_id=m.school_id AND s.stu_id=m.stu_id AND s.session=m.session
            WHERE m.school_id=? AND m.session=? AND m.term=? AND m.month_id=? AND m.year_of_month=? AND s.sec_id=?
            """, schoolId, session, term, month, String.valueOf(year), sectionId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("rows", rows); out.put("workDays", work == null ? "" : work);
        return out;
    }

    public void saveMonthly(String schoolId, String staffUid, String session, int sectionId, int term, int month, int year,
                            List<Map<String, Object>> rows) {
        for (Map<String, Object> row : rows == null ? List.<Map<String, Object>>of() : rows) {
            int studentId = integer(row.get("studentId"));
            BigDecimal attendance = decimal(row.get("attendance"));
            BigDecimal work = decimal(row.get("workDays"));
            if (studentId <= 0) continue;
            if (attendance.compareTo(work) > 0) throw new IllegalArgumentException("Attendance cannot be greater than work days");
            upsertMonth(schoolId, staffUid, session, studentId, term, month, String.valueOf(year), attendance.toPlainString(), work.toPlainString());
        }
    }

    private void upsertMonth(String schoolId, String staffUid, String session, int studentId, int term, int month, String year,
                             String attendance, String workDays) {
        Integer exists = jdbc.queryForObject("SELECT COUNT(*) FROM month_attendance WHERE school_id=? AND stu_id=? AND term=? AND month_id=? AND session=? AND year_of_month=?",
            Integer.class, schoolId, studentId, term, month, session, year);
        if (exists != null && exists > 0) {
            jdbc.update("UPDATE month_attendance SET attendance=?,work_Day=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND stu_id=? AND term=? AND month_id=? AND session=? AND year_of_month=?",
                attendance, workDays, staffUid, schoolId, studentId, term, month, session, year);
        } else {
            jdbc.update("""
                INSERT INTO month_attendance(Stu_id,attendance,work_Day,att_date,session,created_date,created_by,modified_date,modified_by,school_id,term,month_id,year_of_month)
                VALUES(?,?,?,CAST(GETDATE() AS date),?,GETDATE(),?,'','',?,?,?,?)
                """, studentId, attendance, workDays, session, staffUid, schoolId, term, month, year);
        }
    }

    public List<Map<String, Object>> deleteRows(String schoolId, String session, String admissionNo, String from, String to) {
        return jdbc.queryForList("""
            SELECT CONVERT(varchar(50),CAST(att_date AS date),106) AS attendanceDate,att_status AS attendanceStatus
            FROM day_attendance
            WHERE school_id=? AND session=? AND stu_id=(SELECT TOP 1 stu_id FROM TB_StudentMaster WHERE school_id=? AND registration_no=? AND session=?)
              AND CAST(TRY_CONVERT(date,att_date) AS date) BETWEEN CAST(? AS date) AND CAST(? AS date)
            ORDER BY CAST(TRY_CONVERT(date,att_date) AS date)
            """, schoolId, session, schoolId, admissionNo, session, normalizeDate(from).toString(), normalizeDate(to).toString());
    }

    public int deleteRows(String schoolId, String session, String admissionNo, String from, String to, String deletedBy) {
        String f = normalizeDate(from).toString(), t = normalizeDate(to).toString();
        Integer count = jdbc.queryForObject("""
            SELECT COUNT(*) FROM day_attendance
            WHERE school_id=? AND session=? AND stu_id=(SELECT TOP 1 stu_id FROM TB_StudentMaster WHERE school_id=? AND registration_no=? AND session=?)
              AND CAST(TRY_CONVERT(date,att_date) AS date) BETWEEN CAST(? AS date) AND CAST(? AS date)
            """, Integer.class, schoolId, session, schoolId, admissionNo, session, f, t);
        jdbc.update("""
            INSERT INTO day_attendance_log(row_id,Stu_id,att_status,att_date,session,created_date,created_by,modified_date,modified_by,school_id,feestatus,deleted_by,deleted_date)
            SELECT row_id,Stu_id,att_status,att_date,session,created_date,created_by,modified_date,modified_by,school_id,feestatus,?,GETDATE()
            FROM day_attendance
            WHERE school_id=? AND session=? AND stu_id=(SELECT TOP 1 stu_id FROM TB_StudentMaster WHERE school_id=? AND registration_no=? AND session=?)
              AND CAST(TRY_CONVERT(date,att_date) AS date) BETWEEN CAST(? AS date) AND CAST(? AS date)
            """, deletedBy, schoolId, session, schoolId, admissionNo, session, f, t);
        jdbc.update("""
            DELETE FROM day_attendance
            WHERE school_id=? AND session=? AND stu_id=(SELECT TOP 1 stu_id FROM TB_StudentMaster WHERE school_id=? AND registration_no=? AND session=?)
              AND CAST(TRY_CONVERT(date,att_date) AS date) BETWEEN CAST(? AS date) AND CAST(? AS date)
            """, schoolId, session, schoolId, admissionNo, session, f, t);
        return count == null ? 0 : count;
    }

    public List<Map<String, Object>> holidayClasses(String schoolId) { return allClasses(schoolId); }

    public int saveHoliday(String schoolId, String session, List<Integer> classIds, String date, String staffUid) {
        String d = normalizeDate(date).toString(); int inserted = 0;
        for (Integer classId : classIds == null ? List.<Integer>of() : classIds) {
            List<Integer> students = jdbc.query("SELECT DISTINCT stu_id FROM TB_StudentMaster WHERE school_id=? AND session=? AND LOWER(status)='registered' AND class_id=?",
                (rs,n)->rs.getInt(1), schoolId, session, classId);
            for (Integer student : students) {
                int n = jdbc.update("""
                    INSERT INTO day_attendance(Stu_id,att_status,att_date,session,created_date,created_by,modified_by,school_id)
                    SELECT ?, 'H', ?, ?, GETDATE(), ?, '', ?
                    WHERE NOT EXISTS (SELECT 1 FROM day_attendance WHERE school_id=? AND session=? AND stu_id=? AND TRY_CONVERT(date,att_date)=TRY_CONVERT(date,?))
                    """, student, d, session, staffUid, schoolId, schoolId, session, student, d);
                inserted += n;
            }
        }
        return inserted;
    }

    public List<Map<String, Object>> rfidCampusCount(String schoolId, String session, String date, int sectionId) {
        String d = normalizeDate(date).toString();
        String sectionFilter = sectionId > 0 ? " AND s.sec_id=? " : " ";
        List<Object> args = new ArrayList<>(List.of(session, schoolId));
        if (sectionId > 0) args.add(sectionId);
        args.add(schoolId); args.add("Student"); args.add(d); args.add(schoolId);
        String q = """
            WITH students AS (
              SELECT s.class_id,s.sec_id,s.registration_no FROM TB_StudentMaster s
              WHERE s.session=? AND s.school_id=? %s AND s.status IN ('Registered','Withdrawn')
                AND ((s.status='Withdrawn' AND TRY_CONVERT(date,s.Withdrawn_date)>=CAST(? AS date)) OR (s.status='Registered' AND TRY_CONVERT(date,s.Admission_date)<=CAST(? AS date)))
            ), scans AS (
              SELECT EntityId,AttendanceStatus,ROW_NUMBER() OVER(PARTITION BY EntityId,AttendanceStatus,AttendanceType,CAST(AttendanceDate AS date) ORDER BY EntityId) rn
              FROM RFID_Attendance WHERE school_id=? AND UserType=? AND AttendanceType='Campus' AND CAST(AttendanceDate AS date)=CAST(? AS date)
            ), grouped AS (SELECT EntityId,SUM(CASE WHEN AttendanceStatus='IN' THEN 1 ELSE 0 END) AS inCount,SUM(CASE WHEN AttendanceStatus='OUT' THEN 1 ELSE 0 END) AS outCount FROM scans WHERE rn=1 GROUP BY EntityId)
            SELECT s.class_id AS classId,s.sec_id AS sectionId,cn.class_name+' ('+c.section+')' AS class,
                   COUNT(s.registration_no) AS totalCount,COALESCE(SUM(g.inCount),0) AS inCount,COALESCE(SUM(g.outCount),0) AS outCount
            FROM students s LEFT JOIN grouped g ON g.EntityId=s.registration_no
            LEFT JOIN class_name cn ON cn.school_id=? AND cn.id=s.class_id
            LEFT JOIN TB_Class c ON c.school_id=? AND c.sec_id=s.sec_id
            GROUP BY s.class_id,s.sec_id,cn.class_name,c.section ORDER BY s.class_id,s.sec_id
            """.formatted(sectionFilter);
        // Date is used twice in the student eligibility predicate.
        args.add(2, d); args.add(3, d); args.add(5, d);
        // Above list is intentionally rebuilt to avoid positional mistakes in this long query.
        args = new ArrayList<>();
        args.add(session); args.add(schoolId); if (sectionId > 0) args.add(sectionId); args.add(d); args.add(d);
        args.add(schoolId); args.add("Student"); args.add(d); args.add(schoolId); args.add(schoolId);
        return jdbc.queryForList(q, args.toArray());
    }

    public List<Map<String, Object>> rfidCampusStudent(String schoolId, String session, String date, int sectionId) {
        String d = normalizeDate(date).toString();
        String sectionFilter = sectionId > 0 ? " AND s.sec_id=? " : " ";
        String q = """
            WITH students AS (
              SELECT s.stu_id,s.class_id,s.sec_id,s.registration_no,(COALESCE(sf.first_name,'')+' '+COALESCE(sf.mid_name,'')+' '+COALESCE(sf.last_name,'')) AS studentName
              FROM TB_StudentMaster s LEFT JOIN student_other_info sf ON sf.school_id=s.school_id AND sf.registration_no=s.registration_no
              WHERE s.session=? AND s.school_id=? %s AND s.status IN ('Registered','Withdrawn')
                AND ((s.status='Withdrawn' AND TRY_CONVERT(date,s.Withdrawn_date)>=CAST(? AS date)) OR (s.status='Registered' AND TRY_CONVERT(date,s.Admission_date)<=CAST(? AS date)))
            ), scans AS (
              SELECT EntityId,AttendanceStatus,
                     CASE WHEN CAST(AttendanceTime AS time)='00:00:00' THEN '' ELSE FORMAT(CAST(AttendanceTime AS datetime),'hh:mm:ss tt') END AS attendanceTime,
                     ROW_NUMBER() OVER(PARTITION BY EntityId,AttendanceStatus,AttendanceType,CAST(AttendanceDate AS date) ORDER BY EntityId) rn
              FROM RFID_Attendance WHERE school_id=? AND UserType=? AND AttendanceType='Campus' AND CAST(AttendanceDate AS date)=CAST(? AS date)
            )
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,s.studentName,
                   cn.class_name+' ('+c.section+')' AS class,
                   COALESCE(MAX(CASE WHEN x.AttendanceStatus='IN' THEN x.attendanceTime ELSE '' END),'') AS inTime,
                   COALESCE(MAX(CASE WHEN x.AttendanceStatus='OUT' THEN x.attendanceTime ELSE '' END),'') AS outTime
            FROM students s LEFT JOIN scans x ON x.EntityId=s.registration_no AND x.rn=1
            LEFT JOIN class_name cn ON cn.school_id=? AND cn.id=s.class_id
            LEFT JOIN TB_Class c ON c.school_id=? AND c.sec_id=s.sec_id
            GROUP BY s.stu_id,s.registration_no,s.studentName,cn.class_name,c.section,s.class_id,s.sec_id
            ORDER BY s.class_id,s.sec_id
            """.formatted(sectionFilter);
        List<Object> args = new ArrayList<>(List.of(session, schoolId)); if(sectionId>0)args.add(sectionId); args.add(d);args.add(d);args.add(schoolId);args.add("Student");args.add(d);args.add(schoolId);args.add(schoolId);
        return jdbc.queryForList(q,args.toArray());
    }

    public List<Map<String, Object>> rfidTransportCount(String schoolId, String session, String date) {
        String d=normalizeDate(date).toString();
        return jdbc.queryForList("""
            WITH latest AS (SELECT f.*,ROW_NUMBER() OVER(PARTITION BY f.stu_id ORDER BY f.id DESC) rn FROM fee_Assign_Transport f WHERE f.school_id=? AND f.session=? AND f.status=1),
            detail AS (SELECT f.stu_id,f.route_id,r.route_name FROM latest f JOIN Route_Detail r ON r.school_id=f.school_id AND r.route_id=f.route_id WHERE f.rn=1 AND r.school_id=?),
            scans AS (SELECT EntityId,MAX(CASE WHEN AttendanceStatus='BEI' THEN 1 ELSE 0 END) AS eveningIn,MAX(CASE WHEN AttendanceStatus='BMI' THEN 1 ELSE 0 END) AS morningIn,MAX(CASE WHEN AttendanceStatus='BMO' THEN 1 ELSE 0 END) AS morningOut,MAX(CASE WHEN AttendanceStatus='BEO' THEN 1 ELSE 0 END) AS eveningOut FROM (SELECT *,ROW_NUMBER() OVER(PARTITION BY EntityId,AttendanceStatus ORDER BY EntityId,AttendanceStatus) rn FROM RFID_Attendance WHERE school_id=? AND AttendanceType='transport' AND CAST(AttendanceDate AS date)=CAST(? AS date)) x WHERE rn=1 GROUP BY EntityId)
            SELECT COUNT(DISTINCT d.stu_id) AS studentCount,d.route_id AS routeId,d.route_name AS routeName,
                   COALESCE(SUM(s.eveningIn),0) AS eveningIn,COALESCE(SUM(s.morningIn),0) AS morningIn,COALESCE(SUM(s.morningOut),0) AS morningOut,COALESCE(SUM(s.eveningOut),0) AS eveningOut
            FROM detail d JOIN TB_StudentMaster sm ON sm.school_id=? AND sm.stu_id=d.stu_id AND sm.session=?
              LEFT JOIN scans s ON s.EntityId=sm.registration_no
            WHERE ((sm.status='Withdrawn' AND TRY_CONVERT(date,sm.Withdrawn_date)>=CAST(? AS date)) OR (sm.status='Registered' AND TRY_CONVERT(date,sm.Admission_date)<=CAST(? AS date)))
            GROUP BY d.route_id,d.route_name ORDER BY d.route_id
            """, schoolId,session,schoolId,schoolId,d,schoolId,session,d,d);
    }

    public List<Map<String, Object>> rfidTransportStudent(String schoolId, String session, String date, int routeId) {
        String d=normalizeDate(date).toString();
        String routeFilter=routeId>0?" AND f.route_id=? ":" ";
        String q="""
            WITH latest AS (SELECT f.*,ROW_NUMBER() OVER(PARTITION BY f.stu_id ORDER BY f.id DESC) rn FROM fee_Assign_Transport f WHERE f.school_id=? AND f.session=? AND f.status=1 %s),
            detail AS (SELECT f.stu_id,f.route_id,f.stoppage_id,r.route_name,sp.stoppage_name FROM latest f JOIN Route_Detail r ON r.school_id=f.school_id AND r.route_id=f.route_id LEFT JOIN stoppage_detail sp ON sp.school_id=f.school_id AND sp.stoppage_id=f.stoppage_id WHERE f.rn=1),
            scans AS (SELECT EntityId,MAX(CASE WHEN AttendanceStatus='BEI' THEN AttendanceTime END) AS eveningIn,MAX(CASE WHEN AttendanceStatus='BMI' THEN AttendanceTime END) AS morningIn,MAX(CASE WHEN AttendanceStatus='BMO' THEN AttendanceTime END) AS morningOut,MAX(CASE WHEN AttendanceStatus='BEO' THEN AttendanceTime END) AS eveningOut FROM (SELECT *,ROW_NUMBER() OVER(PARTITION BY EntityId,AttendanceStatus ORDER BY EntityId,AttendanceStatus) rn FROM RFID_Attendance WHERE school_id=? AND AttendanceType='transport' AND CAST(AttendanceDate AS date)=CAST(? AS date)) x WHERE rn=1 GROUP BY EntityId)
            SELECT d.stu_id AS studentId,sm.registration_no AS registrationNo,(COALESCE(sf.first_name,'')+' '+COALESCE(sf.mid_name,'')+' '+COALESCE(sf.last_name,'')) AS studentName,
                   cn.class_name+' ('+c.section+')' AS class,d.route_id AS routeId,d.route_name AS routeName,d.stoppage_id AS stoppageId,d.stoppage_name AS stoppageName,
                   CASE WHEN CAST(s.eveningIn AS time)='00:00:00' THEN '' ELSE FORMAT(CAST(s.eveningIn AS datetime),'hh:mm:ss tt') END AS eveningIn,
                   CASE WHEN CAST(s.morningIn AS time)='00:00:00' THEN '' ELSE FORMAT(CAST(s.morningIn AS datetime),'hh:mm:ss tt') END AS morningIn,
                   CASE WHEN CAST(s.morningOut AS time)='00:00:00' THEN '' ELSE FORMAT(CAST(s.morningOut AS datetime),'hh:mm:ss tt') END AS morningOut,
                   CASE WHEN CAST(s.eveningOut AS time)='00:00:00' THEN '' ELSE FORMAT(CAST(s.eveningOut AS datetime),'hh:mm:ss tt') END AS eveningOut
            FROM detail d JOIN TB_StudentMaster sm ON sm.school_id=? AND sm.stu_id=d.stu_id AND sm.session=?
            LEFT JOIN student_other_info sf ON sf.school_id=sm.school_id AND sf.registration_no=sm.registration_no
            LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id LEFT JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            LEFT JOIN scans s ON s.EntityId=sm.registration_no
            WHERE ((sm.status='Withdrawn' AND TRY_CONVERT(date,sm.Withdrawn_date)>=CAST(? AS date)) OR (sm.status='Registered' AND TRY_CONVERT(date,sm.Admission_date)<=CAST(? AS date)))
            ORDER BY studentName
            """.formatted(routeFilter);
        List<Object> args=new ArrayList<>(List.of(schoolId,session));if(routeId>0)args.add(routeId);args.add(schoolId);args.add(d);args.add(schoolId);args.add(session);args.add(d);args.add(d);
        return jdbc.queryForList(q,args.toArray());
    }

    private List<Map<String,Object>> months(String schoolId,String session,Integer classId,Integer selectedTerm){
        if(classId==null)return List.of();
        List<Map<String,Object>> bounds=selectedTerm==null
            ? jdbc.queryForList("SELECT TOP 1 startdate,enddate FROM session_term WHERE school_id=? AND session=? AND class_id=? ORDER BY term",schoolId,session,classId)
            : jdbc.queryForList("SELECT TOP 1 startdate,enddate FROM session_term WHERE school_id=? AND session=? AND class_id=? AND term=? ORDER BY term",schoolId,session,classId,selectedTerm);
        if(bounds.isEmpty())return List.of();
        LocalDate start=((java.sql.Timestamp)bounds.get(0).get("startdate")).toLocalDateTime().toLocalDate().withDayOfMonth(1);
        Object endRaw=bounds.get(0).get("enddate");LocalDate end=endRaw instanceof java.sql.Timestamp t?t.toLocalDateTime().toLocalDate():LocalDate.parse(String.valueOf(endRaw).substring(0,10));
        List<Map<String,Object>> out=new ArrayList<>();for(LocalDate d=start;d.isBefore(end);d=d.plusMonths(1)){Map<String,Object>m=new LinkedHashMap<>();m.put("month",d.getMonthValue());m.put("year",d.getYear());m.put("label",d.getMonth().toString().substring(0,1)+d.getMonth().toString().substring(1).toLowerCase()+" ("+d.getYear()+")");out.add(m);}return out;
    }

    private Integer classId(String schoolId,String session,int sectionId){
        Integer direct=jdbc.queryForObject("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?",Integer.class,schoolId,sectionId);
        if(direct!=null)return direct;
        return jdbc.query("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=(SELECT TOP 1 sec_id FROM TB_VirtualClass WHERE school_id=? AND session=? AND vclass_id=?)",(rs,n)->rs.getInt(1),schoolId,schoolId,session,sectionId).stream().findFirst().orElse(null);
    }

    private static String yearStart(String session){return session==null||session.length()<4?session:session.substring(0,4);}
    private static String yearEnd(String session){return session==null||session.length()<4?session:session.substring(session.length()-4);}
    private static String text(Object o){return o==null?"":String.valueOf(o).trim();}
    private static int integer(Object o){try{return Integer.parseInt(text(o));}catch(Exception e){return 0;}}
    private static BigDecimal decimal(Object o){try{return new BigDecimal(text(o).isBlank()?"0":text(o));}catch(Exception e){throw new IllegalArgumentException("Attendance values must be numeric");}}
    private static LocalDate normalizeDate(String raw){
        if(raw==null||raw.isBlank())throw new IllegalArgumentException("Date is required");String v=raw.trim();
        for(DateTimeFormatter f:List.of(DateTimeFormatter.ISO_LOCAL_DATE,DateTimeFormatter.ofPattern("dd MMM yyyy"),DateTimeFormatter.ofPattern("d MMM yyyy"),DateTimeFormatter.ofPattern("dd/MM/yyyy"),DateTimeFormatter.ofPattern("MM/dd/yyyy"))){try{return LocalDate.parse(v,f);}catch(DateTimeParseException ignored){}}
        throw new IllegalArgumentException("Invalid date: "+raw);
    }
    private String firstString(String sql,Object...args){List<String>r=jdbc.query(sql,(rs,n)->rs.getString(1),args);return r.isEmpty()?null:r.get(0);}
}
