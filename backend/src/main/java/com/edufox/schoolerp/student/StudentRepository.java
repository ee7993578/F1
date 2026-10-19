package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class StudentRepository {
    private static final int MAX_ROWS = 1000;
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap(String schoolId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("classes", classes(schoolId));
        result.put("sections", sections(schoolId));
        result.put("sessions", sessions(schoolId));
        result.put("houses", houses(schoolId));
        result.put("students", search(schoolId, null, null, null, null, null));
        result.put("summary", summary(schoolId, null));
        return result;
    }

    public List<Map<String, Object>> classes(String schoolId) {
        return jdbc.queryForList("""
            SELECT id AS classId, class_name AS className, COALESCE(displayClassName,class_name) AS displayClassName, class_group AS classGroup
            FROM class_name
            WHERE school_id=?
            ORDER BY id
            """, schoolId);
    }

    public List<Map<String, Object>> sections(String schoolId) {
        return jdbc.queryForList("""
            SELECT c.sec_id AS sectionId, c.class_id AS classId, c.section AS sectionName,
                   COALESCE(n.class_name,'') AS className
            FROM TB_Class c
            LEFT JOIN class_name n ON n.id=c.class_id AND n.school_id=c.school_id
            WHERE c.school_id=?
            ORDER BY c.class_id, c.section, c.sec_id
            """, schoolId);
    }

    public List<Map<String, Object>> sessions(String schoolId) {
        return jdbc.queryForList("""
            SELECT id, session, status
            FROM TB_Session
            WHERE school_id=?
            ORDER BY id DESC
            """, schoolId);
    }

    /** BindStaffClasses: entitytype=other, type=class. No stored procedure execution. */
    public List<Map<String, Object>> imageUploadSections(String schoolId, String staffUid, String session) {
        List<String> facultyIds = jdbc.queryForList(
            "SELECT CAST(f_id AS varchar(50)) FROM TB_FacultyMaster WHERE school_id=? AND staff_u_id=?",
            String.class, schoolId, staffUid);
        if (facultyIds.isEmpty()) return List.of();
        String facultyId = facultyIds.get(facultyIds.size() - 1);
        List<String> permissions = jdbc.queryForList("""
            SELECT DISTINCT class_id FROM roles_permission_efox
            WHERE school_id=? AND Status=1 AND class_id IS NOT NULL AND class_id<>''
              AND Desi_Id IN (SELECT Desg_id FROM desig_mapping WHERE school_id=? AND f_id=?)
            """, String.class, schoolId, schoolId, facultyId);
        Set<String> allowedClasses = new HashSet<>();
        for (String permission : permissions) {
            for (String id : permission.split(",")) {
                if (!id.isBlank()) allowedClasses.add(id.trim());
            }
        }
        Set<String> teacherSections = new HashSet<>(jdbc.queryForList("""
            SELECT CAST(section_id AS varchar(50)) FROM Tb_ClassTeacher
            WHERE school_id=? AND session=? AND f_id=?
            """, String.class, schoolId, session, facultyId));
        // The legacy procedure uses the first/last four characters of the session.
        List<Map<String, Object>> mappedSections = jdbc.queryForList("""
            SELECT c.sec_id AS sectionId, c.class_id AS classId, c.section AS sectionName,
                   n.class_name AS className
            FROM TB_Class c
            LEFT JOIN class_name n ON n.school_id=c.school_id AND n.id=c.class_id
            WHERE c.school_id=? AND EXISTS (
                SELECT 1 FROM classsessionmapping m
                WHERE m.school_id=c.school_id AND m.sec_id=c.sec_id AND m.status=1
                  AND m.from_year<=? AND (m.to_year='0' OR (m.to_year<>'0' AND m.to_year>=?)))
            ORDER BY c.class_id, c.sec_id
            """, schoolId, session.substring(0, 4), session.substring(session.length() - 4));
        return mappedSections.stream().filter(row ->
            allowedClasses.contains(String.valueOf(row.get("classId")))
                || teacherSections.contains(String.valueOf(row.get("sectionId")))).toList();
    }

    public List<Map<String, Object>> infosheetLookup(String schoolId, String session, String status,
                                                    String registrationNo, String feeBookNo) {
        String sql = baseSelect() + " AND sm.session=? AND LOWER(sm.status)=LOWER(?)"
            + (registrationNo != null ? " AND sm.registration_no=?" : " AND sm.FeeBookNo=?")
            + " ORDER BY sm.stu_id";
        return jdbc.queryForList(sql, schoolId, session, status,
            registrationNo != null ? registrationNo : feeBookNo);
    }

    public List<Map<String, Object>> houses(String schoolId) {
        return jdbc.queryForList("""
            SELECT House_id AS houseId, H_Name AS houseName
            FROM HouseMaster
            WHERE school_id=? AND status=1
            ORDER BY id
            """, schoolId);
    }

    public List<Map<String, Object>> terms() {
        return jdbc.queryForList("SELECT term_id AS id, term_name AS name FROM term ORDER BY term_id");
    }

    /** Database-backed equivalent of the legacy bindterm/show_term_exam calls. */
    public Map<String, Object> academicOptions(String schoolId, String session, Integer classId, String termId) {
        if (session == null || session.isBlank() || classId == null) {
            return Map.of("terms", List.of(), "exams", List.of());
        }
        String yearStart = session.length() >= 4 ? session.substring(0, 4) : session;
        // assign_exam_set.to_year stores the full ending year (the legacy
        // show_term_exam query compares it with RIGHT(session,4)).
        String yearEnd = session.length() >= 4 ? session.substring(session.length() - 4) : session;
        List<Map<String, Object>> terms = jdbc.queryForList("""
            SELECT DISTINCT e.term_id AS id,
                   COALESCE(t.term_name, CAST(e.term_id AS varchar(50))) AS name
            FROM exam_set e
            JOIN assign_exam_set a ON a.exam_set_id=e.exam_set_id
            LEFT JOIN term t ON t.term_id=e.term_id
            WHERE e.school_id=? AND a.school_id=? AND a.class_id=?
              AND a.from_year<=? AND (a.to_year='0' OR a.to_year>=?)
            ORDER BY e.term_id
            """, schoolId, schoolId, classId, yearStart, yearEnd);

        List<Map<String, Object>> exams = List.of();
        if (termId != null && !termId.isBlank() && !"0".equals(termId)) {
            exams = jdbc.queryForList("""
                SELECT e.exam_type_id AS id, e.exam_name_dis AS name,
                       e.exam_order AS displayOrder
                FROM exam_set e
                JOIN assign_exam_set a ON a.exam_set_id=e.exam_set_id
                WHERE e.school_id=? AND a.school_id=? AND a.class_id=?
                  AND a.from_year<=? AND (a.to_year='0' OR a.to_year>=?)
                  AND e.term_id=?
                ORDER BY e.exam_order, e.exam_type_id
                """, schoolId, schoolId, classId, yearStart, yearEnd, termId);
        }
        return Map.of("terms", terms, "exams", exams);
    }

    public Map<String, Object> registrationOptions(String schoolId) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("schoolId", schoolId);
        out.put("mops", jdbc.queryForList("SELECT pay_mode AS name FROM Tb_mopmaster ORDER BY pay_mode"));
        out.put("quota", jdbc.queryForList("SELECT DISTINCT id, quota AS name FROM quota WHERE school_id=? ORDER BY id", schoolId));
        out.put("blood", jdbc.queryForList("SELECT id, blood_group AS name FROM blood_group ORDER BY id"));
        out.put("categories", jdbc.queryForList("SELECT id, category_name AS name FROM category ORDER BY id"));
        out.put("religions", jdbc.queryForList("SELECT id, religion AS name FROM religion ORDER BY id"));
        out.put("types", jdbc.queryForList("SELECT id, schooltype AS name FROM schooltype WHERE id<>3 ORDER BY id"));
        out.put("states", jdbc.queryForList("SELECT state_id AS id, state_name AS name FROM state ORDER BY state_name"));
        out.put("cities", jdbc.queryForList("SELECT city_id AS id, UPPER(city_name) AS name, state_id AS stateId FROM Districts WHERE country_id=1 ORDER BY city_name"));
        out.put("banks", jdbc.queryForList("SELECT Bank_Id AS id, BankName AS name FROM TB_Bank ORDER BY BankName"));
        out.put("occupations", jdbc.queryForList("SELECT ocp_code AS id, occupation AS name FROM Occupation ORDER BY occupation"));
        out.put("streams", jdbc.queryForList("""
            SELECT DISTINCT sm.fee_head_id AS id, s.stream_name AS name
            FROM tb_stream_mapping sm JOIN tb_stream s ON s.stream_id=sm.stream_id
            WHERE sm.school_id=? ORDER BY s.stream_name
            """, schoolId));
        out.put("optionalSubjects", jdbc.queryForList("""
            SELECT DISTINCT id AS id, subject AS name
            FROM TB_SubjectMaster WHERE school_id=? ORDER BY subject
            """, schoolId));
        out.put("dobYears", jdbc.queryForList("""
            WITH yearlist AS (SELECT 1998 AS year UNION ALL SELECT year+1 FROM yearlist WHERE year+1<=YEAR(GETDATE()))
            SELECT year FROM yearlist ORDER BY year DESC OPTION (MAXRECURSION 200)
            """));
        // These are the same per-school defaults that the legacy registration
        // page applied in Page_Load/BindStateCity.  They are deliberately
        // read from the database instead of being repeated in the React form.
        out.put("registrationDefaults", jdbc.queryForList("""
            SELECT TOP 1 sm.schooltype AS schoolType,
                         c.select_state AS selectState,
                         c.select_city AS selectCity,
                         COALESCE(c.registrationMOP, 0) AS registrationMop
            FROM schoolmaster sm
            LEFT JOIN configuration c ON c.school_id=sm.school_id
            WHERE sm.school_id=?
            """, schoolId));
        Object nextForm = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int, form_no))+1,1001) FROM student_reg WHERE school_id=?", Object.class, schoolId);
        out.put("nextFormNo", nextForm == null ? "1001" : String.valueOf(nextForm));
        return out;
    }

    /** Direct SQL equivalent of the legacy adm_no_allotment row lookups. */
    public Map<String, Object> admissionAllotmentOptions(String schoolId, String session, Integer classId) {
        if (session == null || session.isBlank() || classId == null) {
            return Map.of("sections", List.of(), "concessions", List.of(), "streams", List.of(),
                    "optionalSubjects", List.of(), "lastAdmissionNo", "0", "lastFeeBookNo", "0");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sections", jdbc.queryForList("""
            SELECT sec_id AS id, section AS name
            FROM TB_Class
            WHERE school_id=? AND class_id=?
            ORDER BY sec_id
            """, schoolId, classId));
        result.put("concessions", jdbc.queryForList("""
            SELECT DISTINCT g.group_code AS id, g.group_name AS name
            FROM concession_group_name g
            WHERE g.school_id=? AND g.session=?
              AND EXISTS (
                  SELECT 1 FROM concession_group_details d
                  WHERE d.school_id=g.school_id AND d.session=g.session
                    AND d.group_code=g.group_code
              )
            ORDER BY g.group_name
            """, schoolId, session));
        result.put("streams", jdbc.queryForList("""
            SELECT DISTINCT sm.fee_head_id AS id, s.stream_name AS name
            FROM tb_stream_mapping sm
            JOIN tb_stream s ON s.stream_id=sm.stream_id
            WHERE sm.school_id=? AND sm.session=?
            ORDER BY s.stream_name
            """, schoolId, session));
        result.put("optionalSubjects", jdbc.queryForList("""
            SELECT DISTINCT cs.subject_id AS id, sm.subject AS name
            FROM TB_ClassSubject cs
            JOIN TB_SubjectMaster sm ON sm.school_id=cs.school_id AND sm.id=cs.subject_id
            JOIN TB_Class sec ON sec.school_id=cs.school_id AND sec.sec_id=cs.class_id
            WHERE cs.school_id=? AND sec.class_id=? AND cs.isoptional=1
              AND cs.isprac='N' AND cs.status=1
            ORDER BY sm.subject
            """, schoolId, classId));
        result.put("lastAdmissionNo", numericMax(schoolId, session, "registration_no"));
        result.put("lastFeeBookNo", numericMax(schoolId, session, "FeeBookNo"));
        return result;
    }

    private String numericMax(String schoolId, String session, String column) {
        String safeColumn = switch (column) {
            case "registration_no", "FeeBookNo" -> column;
            default -> throw new IllegalArgumentException("Unsupported numeric column");
        };
        Object value = jdbc.queryForObject("""
            SELECT COALESCE(MAX(TRY_CONVERT(int, %s)),0)
            FROM TB_StudentMaster
            WHERE school_id=? AND session=?
            """.formatted(safeColumn), Object.class, schoolId, session);
        return value == null ? "0" : String.valueOf(value);
    }

    public List<Map<String, Object>> search(String schoolId, String query, Integer classId,
                                            Integer sectionId, String status, String session) {
        StringBuilder sql = new StringBuilder(baseSelect());
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        appendFilters(sql, args, query, classId, sectionId, status, session);
        sql.append(" ORDER BY COALESCE(so.first_name,''), COALESCE(so.last_name,''), sm.stu_id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public Map<String, Object> findOne(String schoolId, String studentId) {
        String sql = baseSelect() + """
            AND (CAST(sm.stu_id AS varchar(50))=? OR CAST(sm.uid AS varchar(100))=?
                 OR sm.registration_no=?)
            ORDER BY sm.stu_id
            """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, schoolId, studentId, studentId, studentId);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    public Map<String, Object> registrationDetail(String schoolId, long rowId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT sr.row_id AS id, sr.uid AS uid, sr.form_no AS formNo,
                   sr.session AS session, sr.Registration_date AS registrationDate,
                   sr.receipt_no AS receiptNo, sr.mop AS paymentMode,
                   sr.class_id AS classId, COALESCE(c.class_name,'') AS className,
                   COALESCE(so.first_name, sr.first_name,'') AS firstName,
                   COALESCE(so.mid_name, sr.mid_name,'') AS middleName,
                   COALESCE(so.last_name, sr.last_name,'') AS lastName,
                   COALESCE(so.father_name, sr.father_name,'') AS fatherName,
                   COALESCE(so.Mother_name, sr.mother_name,'') AS motherName,
                   COALESCE(so.dob, sr.dob) AS dob,
                   COALESCE(so.gender, sr.gender,'') AS gender,
                   COALESCE(so.mobile, sr.mobile,'') AS mobile,
                   COALESCE(so.phone, sr.phone,'') AS phone,
                   COALESCE(so.R_Address, sr.r_address,'') AS address,
                   COALESCE(so.bloodgroup,'') AS bloodGroup,
                   COALESCE(so.quota,'') AS quota,
                   COALESCE(so.student_aadhar,'') AS studentAadhar,
                   COALESCE(so.student_E_mail,'') AS studentEmail
            FROM student_reg sr
            LEFT JOIN student_other_info so ON so.uid=sr.uid AND so.school_id=sr.school_id
            LEFT JOIN class_name c ON c.id=sr.class_id AND c.school_id=sr.school_id
            WHERE sr.school_id=? AND sr.row_id=?
            """, schoolId, rowId);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    public List<Map<String, Object>> report(String schoolId, String type, String query,
                                            Integer classId, Integer sectionId, String status,
                                            String session, Integer month, String studentType,
                                            String fromDate, String toDate, Integer fromDay, Integer fromMonth,
                                            Integer toDay, Integer toMonth) {
        String normalized = type == null ? "student-list" : type.trim().toLowerCase();
        if (normalized.equals("online-registration") || normalized.equals("online-registration-status")) {
            return onlineRegistrationReport(schoolId, session, classId, month, studentType,
                query, status, normalized.equals("online-registration-status"));
        }
        if (normalized.equals("entrance-admit-card")) {
            return entranceAdmitCards(schoolId, session, classId, studentType);
        }
        if (normalized.equals("admitted")) {
            return admittedStudents(schoolId, session);
        }
        if (normalized.equals("new-student")) {
            return newStudentReport(schoolId, session, sectionId, fromDate, toDate);
        }
        if (normalized.equals("registration") || normalized.equals("registration-details")) {
            return registrationSearch(schoolId, query, classId, sectionId, status, session,
                false, fromDate, toDate);
        }
        if (normalized.equals("strength")) {
            return strengthExact(schoolId, classId, sectionId, session);
        }
        if (normalized.equals("classwise-strength")) {
            return classwiseStrength(schoolId, classId, session);
        }
        if (normalized.equals("all-student-strength")) {
            return allStudentStrength(schoolId, session);
        }
        if (normalized.equals("birthday")) {
            return birthdayReport(schoolId, session, studentType);
        }
        if (normalized.equals("student-type")) {
            return studentTypeReport(schoolId, session, studentType);
        }
        if (normalized.equals("udise-promotion")) {
            return udisePromotion(schoolId, classId, sectionId, session);
        }
        if (normalized.equals("notpromoted")) {
            return notPromoted(schoolId, session);
        }
        if (normalized.equals("section_notalloted")) {
            return sectionNotAlloted(schoolId, session);
        }

        StringBuilder sql = new StringBuilder(baseSelect());
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        appendFilters(sql, args, query, classId, sectionId, status, session);

        switch (normalized) {
            case "withdrawn" -> sql.append(" AND LOWER(sm.status)='withdrawn'");
            case "withdrawn-management" -> { /* old update page loads both Registered and Withdrawn */ }
            case "birthday" -> {
                if (month != null && month >= 1 && month <= 12) {
                    sql.append(" AND MONTH(so.dob)=?");
                    args.add(month);
                }
            }
            case "house", "sibling", "registration", "registration-details",
                 "student-record", "student-list", "id-password" -> {
                if (normalized.equals("student-type") && studentType != null && !studentType.isBlank()) {
                    sql.append(" AND CAST(sm.stutype AS varchar(20))=?");
                    args.add(studentType.trim());
                }
            }
            default -> throw new IllegalArgumentException("Unsupported student report type");
        }

        sql.append(" ORDER BY COALESCE(so.first_name,''), COALESCE(so.last_name,''), sm.stu_id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    /** Direct equivalent of report_admitted_student. These are registrations
     * which are not yet present in TB_StudentMaster for the selected session. */
    private List<Map<String, Object>> admittedStudents(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT sr.row_id AS studentId, sr.uid, sr.form_no AS registrationNo,
                   sr.class_id AS classId, COALESCE(c.class_name,'') AS className,
                   COALESCE(so.first_name,'') AS firstName, COALESCE(so.mid_name,'') AS middleName,
                   COALESCE(so.last_name,'') AS lastName, COALESCE(so.father_name,'') AS fatherName,
                   COALESCE(so.mother_name,'') AS motherName, so.dob, COALESCE(so.r_address,'') AS address,
                   COALESCE(so.father_e_mail,'') AS email, COALESCE(so.phone,'') AS phone,
                   COALESCE(so.mobile,'') AS mobile, sr.status, sr.Registration_date AS registrationDate,
                   sr.reg_fee AS regFee, sr.receipt_no AS receiptNo, sr.session
            FROM student_reg sr
            LEFT JOIN student_other_info so ON so.school_id=sr.school_id AND so.uid=sr.uid
            LEFT JOIN class_name c ON c.school_id=sr.school_id AND c.id=sr.class_id
            WHERE sr.school_id=? AND sr.session=?
              AND NOT EXISTS (SELECT 1 FROM TB_StudentMaster sm
                              WHERE sm.school_id=sr.school_id AND sm.session=sr.session
                                AND sm.uid=sr.uid)
            ORDER BY sr.class_id, COALESCE(so.first_name,''), COALESCE(so.last_name,'')
            """, schoolId, session == null ? "" : session);
    }

    /** Direct equivalent of Student_Type. The legacy page reads student type
     * from student_reg and returns only registered applications. */
    private List<Map<String, Object>> studentTypeReport(String schoolId, String session, String studentType) {
        return jdbc.queryForList("""
            SELECT sr.form_no AS registration_no,
                   LTRIM(RTRIM(CONCAT(COALESCE(so.first_name,''),' ',
                                      COALESCE(so.mid_name,''),' ',
                                      COALESCE(so.last_name,'')))) AS Student_Name,
                   COALESCE(so.father_name,'') AS father_name,
                   so.dob AS dob,
                   CONCAT(COALESCE(c.class_name,''),
                          CASE WHEN COALESCE(sc.section,'')='' THEN '' ELSE CONCAT(' (',sc.section,')') END) AS class_name,
                   COALESCE(sc.section,'') AS section,
                   COALESCE(so.mobile,so.phone,'') AS mobile,
                   sr.status AS status
            FROM student_reg sr
            LEFT JOIN student_other_info so ON so.uid=sr.uid AND so.school_id=sr.school_id
            LEFT JOIN class_name c ON c.id=sr.class_id AND c.school_id=sr.school_id
            OUTER APPLY (
                SELECT TOP 1 m.sec_id
                FROM TB_StudentMaster m
                WHERE m.school_id=sr.school_id AND m.uid=sr.uid AND m.session=sr.session
                ORDER BY m.stu_id DESC
            ) sm
            LEFT JOIN TB_Class sc ON sc.sec_id=sm.sec_id AND sc.school_id=sr.school_id
            WHERE sr.school_id=? AND sr.session=? AND sr.stutype=? AND sr.status='Registered'
            ORDER BY LTRIM(RTRIM(CONCAT(COALESCE(so.first_name,''),' ',
                                        COALESCE(so.mid_name,''),' ',COALESCE(so.last_name,''))))
            """, schoolId, session == null ? "" : session, studentType == null ? "" : studentType);
    }

    /** Direct equivalent of getEntranceAdmitCardDetails. Only paid online
     * applications joined to the configured entrance exam are eligible. */
    private List<Map<String, Object>> entranceAdmitCards(String schoolId, String session,
                                                          Integer classId, String studentType) {
        StringBuilder sql = new StringBuilder("""
            SELECT osr.id AS studentId, osr.formNo AS formNo,
                   osr.registrationNo AS registrationNo,
                   LTRIM(RTRIM(CONCAT(COALESCE(osr.firstName,''),' ',COALESCE(osr.midName,''),' ',COALESCE(osr.lastName,'')))) AS name,
                   COALESCE(osr.fatherName,'') AS fatherName, COALESCE(osr.motherName,'') AS motherName,
                   osr.DOB AS dob,
                   COALESCE(c.displayClassName,c.class_name,'') AS className,
                   osr.admissionClass AS classId, osr.session, COALESCE(osr.studentType,'') AS studentType,
                   ec.examDate, ec.examReportingTime AS reportingTime,
                   CONCAT('GATE No. ',COALESCE(ec.examEntryGateNO,'')) AS entryGate,
                   CONCAT(COALESCE(ec.examStartTime,''),' to ',COALESCE(ec.examEndTime,'')) AS examTime
            FROM onlineStudentRegistration osr
            JOIN entranceExamConfiguration ec
              ON osr.school_id=ec.schoolId AND osr.admissionClass=ec.classId AND osr.session=ec.examSession
            LEFT JOIN class_name c ON c.school_id=osr.school_id
                                  AND TRY_CONVERT(int,osr.admissionClass)=c.id
            WHERE osr.school_id=? AND osr.session=? AND osr.paymentStatus='Success'
            """);
        List<Object> args = new ArrayList<>(); args.add(schoolId); args.add(session);
        if (classId != null) { sql.append(" AND TRY_CONVERT(int,osr.admissionClass)=?"); args.add(classId); }
        if (studentType != null && !studentType.isBlank() && !"0".equals(studentType)) {
            sql.append(" AND osr.studentType=?"); args.add(studentType);
        }
        sql.append(" ORDER BY osr.id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    /** Direct equivalent of report_newstudent. */
    private List<Map<String, Object>> newStudentReport(String schoolId, String session, Integer sectionId,
                                                       String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT sm.stu_id AS studentId, sm.uid, sm.registration_no AS registrationNo,
                   sm.class_id AS classId, sm.sec_id AS sectionId,
                   COALESCE(c.class_name,'') AS className, COALESCE(sc.section,'') AS sectionName,
                   COALESCE(so.first_name,'') AS firstName, COALESCE(so.mid_name,'') AS middleName,
                   COALESCE(so.last_name,'') AS lastName, COALESCE(so.father_name,'') AS fatherName,
                   COALESCE(so.mobile,'') AS mobile, so.dob, so.r_address AS address,
                   sm.Admission_date AS admissionDate, sm.Registration_date AS registrationDate,
                   so.gender, COALESCE(sr.reg_fee,0) AS regFee, sm.session, sm.status
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid
            LEFT JOIN student_reg sr ON sr.school_id=sm.school_id AND sr.uid=sm.uid AND sr.session=sm.session
            LEFT JOIN class_name c ON c.school_id=sm.school_id AND c.id=sm.class_id
            LEFT JOIN TB_Class sc ON sc.school_id=sm.school_id AND sc.sec_id=sm.sec_id
            WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND sm.isnew=1
            """);
        List<Object> args = new ArrayList<>();
        args.add(schoolId); args.add(session);
        if (sectionId != null) { sql.append(" AND sm.sec_id=?"); args.add(sectionId); }
        if (fromDate != null && !fromDate.isBlank()) { sql.append(" AND TRY_CONVERT(date,sm.Registration_date)>=TRY_CONVERT(date,?,23)"); args.add(fromDate); }
        if (toDate != null && !toDate.isBlank()) { sql.append(" AND TRY_CONVERT(date,sm.Registration_date)<=TRY_CONVERT(date,?,23)"); args.add(toDate); }
        sql.append(" ORDER BY sm.class_id,sm.sec_id,COALESCE(so.first_name,'')");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    /** Direct equivalent of udp_ClassWise_strength. */
    private List<Map<String, Object>> classwiseStrength(String schoolId, Integer classId, String session) {
        StringBuilder sql = new StringBuilder("""
            SELECT c.id AS classId, c.class_name AS className,
                   COUNT(sm.stu_id) AS studentCount,
                   SUM(CASE WHEN sm.isnew=1 THEN 1 ELSE 0 END) AS newAdmission,
                   SUM(CASE WHEN sm.isnew=0 THEN 1 ELSE 0 END) AS oldAdmission,
                   SUM(CASE WHEN LOWER(COALESCE(so.gender,''))='male' THEN 1 ELSE 0 END) AS boys,
                   SUM(CASE WHEN LOWER(COALESCE(so.gender,''))='female' THEN 1 ELSE 0 END) AS girls,
                   SUM(CASE WHEN COALESCE(so.RTE,0)=1 THEN 1 ELSE 0 END) AS rte
            FROM class_name c
            JOIN TB_StudentMaster sm ON sm.school_id=c.school_id AND sm.class_id=c.id
            LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid
            WHERE c.school_id=? AND sm.session=? AND LOWER(sm.status)='registered'
            """);
        List<Object> args = new ArrayList<>(); args.add(schoolId); args.add(session);
        if (classId != null) { sql.append(" AND c.id=?"); args.add(classId); }
        sql.append(" GROUP BY c.id,c.class_name ORDER BY c.id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    /** Direct equivalent of strength_report, including class teacher and totals. */
    private List<Map<String, Object>> strengthExact(String schoolId, Integer classId, Integer sectionId,
                                                     String session) {
        StringBuilder sql = new StringBuilder("""
            SELECT sm.class_id AS classId, COALESCE(c.class_name,'') AS className,
                   sm.sec_id AS sectionId, COALESCE(sc.section,'') AS sectionName,
                   COUNT(*) AS studentCount,
                   SUM(CASE WHEN sm.isnew=1 THEN 1 ELSE 0 END) AS newAdmission,
                   SUM(CASE WHEN sm.isnew=0 THEN 1 ELSE 0 END) AS oldAdmission,
                   SUM(CASE WHEN LOWER(COALESCE(so.gender,''))='male' THEN 1 ELSE 0 END) AS boys,
                   SUM(CASE WHEN LOWER(COALESCE(so.gender,''))='female' THEN 1 ELSE 0 END) AS girls,
                   MAX(COALESCE(LTRIM(RTRIM(CONCAT(f.First_name,' ',f.Mid_name,' ',f.Last_name))),'')) AS classTeacher
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid
            LEFT JOIN class_name c ON c.school_id=sm.school_id AND c.id=sm.class_id
            LEFT JOIN TB_Class sc ON sc.school_id=sm.school_id AND sc.sec_id=sm.sec_id
            LEFT JOIN Tb_ClassTeacher ct ON ct.school_id=sm.school_id AND ct.session=sm.session AND ct.section_id=sm.sec_id
            LEFT JOIN TB_FacultyMaster f ON f.school_id=ct.school_id AND f.F_id=ct.f_id
            WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered'
            """);
        List<Object> args = new ArrayList<>(); args.add(schoolId); args.add(session);
        if (classId != null) { sql.append(" AND sm.class_id=?"); args.add(classId); }
        if (sectionId != null) { sql.append(" AND sm.sec_id=?"); args.add(sectionId); }
        sql.append(" GROUP BY sm.class_id,c.class_name,sm.sec_id,sc.section ORDER BY sm.class_id,sm.sec_id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    private List<Map<String, Object>> allStudentStrength(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT sm.class_id AS classId, COALESCE(c.class_name,'') AS className,
                   sm.sec_id AS sectionId, COALESCE(sc.section,'') AS sectionName,
                   SUM(CASE WHEN sm.isnew=1 AND LOWER(sm.status)='registered' THEN 1 ELSE 0 END) AS newAdmission,
                   SUM(CASE WHEN sm.isnew=0 AND LOWER(sm.status)='registered' THEN 1 ELSE 0 END) AS oldAdmission,
                   SUM(CASE WHEN COALESCE(so.RTE,0)=1 AND LOWER(sm.status)='registered' THEN 1 ELSE 0 END) AS rte,
                   SUM(CASE WHEN LOWER(sm.status)='registered' THEN 1 ELSE 0 END) AS studentCount,
                   SUM(CASE WHEN LOWER(sm.status)='withdrawn' THEN 1 ELSE 0 END) AS withdrawnStudent
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid
            LEFT JOIN class_name c ON c.school_id=sm.school_id AND c.id=sm.class_id
            LEFT JOIN TB_Class sc ON sc.school_id=sm.school_id AND sc.sec_id=sm.sec_id
            WHERE sm.school_id=? AND sm.session=?
            GROUP BY sm.class_id,c.class_name,sm.sec_id,sc.section
            ORDER BY sm.class_id,sm.sec_id
            """, schoolId, session);
    }

    private List<Map<String, Object>> birthdayReport(String schoolId, String session, String studentType) {
        if ("faculty".equalsIgnoreCase(studentType)) return jdbc.queryForList("""
            SELECT f.F_id AS studentId, LTRIM(RTRIM(CONCAT(COALESCE(f.First_name,''),' ',COALESCE(f.Mid_name,''),' ',COALESCE(f.Last_name,'')))) AS name,
                   f.DOB AS dob, COALESCE(f.Mobile,f.Phone,'') AS mobile, 'Faculty' AS type,
                   COALESCE(f.Department,'') AS className
            FROM TB_FacultyMaster f
            LEFT JOIN tb_faculty_withdrawn fw ON fw.f_id=f.F_id AND fw.school_id=f.school_id AND fw.session=?
            WHERE f.school_id=? AND f.DOB IS NOT NULL AND (fw.status IS NULL OR LOWER(fw.status)='registered')
            ORDER BY MONTH(f.DOB),DAY(f.DOB),f.First_name
            """, session, schoolId);
        return jdbc.queryForList("""
            SELECT sm.stu_id AS studentId, sm.uid, sm.registration_no AS registrationNo,
                   LTRIM(RTRIM(CONCAT(COALESCE(so.first_name,''),' ',COALESCE(so.mid_name,''),' ',COALESCE(so.last_name,'')))) AS name,
                   so.DOB AS dob, COALESCE(c.class_name,'')+' ('+COALESCE(sc.section,'')+')' AS className,
                   COALESCE(so.mobile,so.phone,'') AS mobile, 'Student' AS type, sm.session
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid
            LEFT JOIN class_name c ON c.school_id=sm.school_id AND c.id=sm.class_id
            LEFT JOIN TB_Class sc ON sc.school_id=sm.school_id AND sc.sec_id=sm.sec_id
            WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered' AND so.DOB IS NOT NULL
            ORDER BY MONTH(so.DOB),DAY(so.DOB),name
            """, schoolId, session);
    }

    private List<Map<String, Object>> notPromoted(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT sm.registration_no AS registrationNo,
                   LTRIM(RTRIM(CONCAT(COALESCE(so.first_name,''),' ',COALESCE(so.mid_name,''),' ',COALESCE(so.last_name,'')))) AS name,
                   COALESCE(c.class_name,'')+' ('+COALESCE(sc.section,'')+')' AS className,
                   sm.session, sm.uid
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid
            LEFT JOIN TB_Class sc ON sc.school_id=sm.school_id AND sc.sec_id=sm.sec_id
            LEFT JOIN class_name c ON c.school_id=sm.school_id AND c.id=sm.class_id
            WHERE sm.school_id=? AND LOWER(sm.status)='registered'
              AND sm.session=(SELECT TOP 1 CAST(Session_year_start-1 AS varchar(4))+' - '+CAST(Session_Year_End-1 AS varchar(4))
                              FROM TB_Session WHERE school_id=? AND session=?)
              AND NOT EXISTS (SELECT 1 FROM TB_StudentMaster nextSm
                              WHERE nextSm.school_id=sm.school_id AND nextSm.session=?
                                AND nextSm.registration_no=sm.registration_no)
            ORDER BY sm.class_id,sm.sec_id,name
            """, schoolId, schoolId, session, session);
    }

    private List<Map<String, Object>> sectionNotAlloted(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT sm.registration_no AS registrationNo,
                   LTRIM(RTRIM(CONCAT(COALESCE(so.first_name,''),' ',COALESCE(so.mid_name,''),' ',COALESCE(so.last_name,'')))) AS name,
                   COALESCE(c.class_name,'')+' ('+COALESCE(sc.section,'')+')' AS className,
                   sm.session, sm.uid
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid
            LEFT JOIN TB_Class sc ON sc.school_id=sm.school_id AND sc.sec_id=sm.sec_id
            LEFT JOIN class_name c ON c.school_id=sm.school_id AND c.id=sm.class_id
            WHERE sm.school_id=? AND sm.session=? AND LOWER(sm.status)='registered'
              AND LOWER(COALESCE(sc.section,''))='new'
            ORDER BY sm.class_id,name
            """, schoolId, session);
    }

    private List<Map<String, Object>> onlineRegistrationReport(String schoolId, String session, Integer classId,
                                                                Integer month, String studentType, String query,
                                                                String status, boolean statusReport) {
        StringBuilder sql = new StringBuilder("""
            SELECT o.id, o.uid, o.formNo AS formNo, o.regPhone AS registrationPhone,
                   o.admissionClass AS classId, COALESCE(c.class_name,'') AS className,
                   o.firstName, o.midName AS middleName, o.lastName, o.DOB AS dob,
                   o.studentEmail, o.gender, o.fatherName, o.fatherMobile, o.fatherEmail,
                   o.motherName, o.motherPhone, o.motherEmail, o.residentialAddr AS address,
                   o.contactPerName, o.contactPhone, o.stream, o.formStatus,
                   o.createdate AS applicationDate, o.session, o.registrationNO AS onlineRegNo,
                   o.paymentAmount, o.paymentDate, o.paymentStatus, o.paymentMode,
                   o.order_id AS orderId, o.reveiptNo AS receiptNo, o.Settlement_Date AS settlementDate,
                   o.AdmissionStatus AS admissionStatus, o.AdmissionStatus AS approvalStatus,
                   COALESCE(o.AdmissionNo,o.stuAdmissionNo,'') AS admissionNo, o.studentType
            FROM onlineStudentRegistration o
            LEFT JOIN class_name c ON c.school_id=o.school_id AND TRY_CONVERT(int,o.admissionClass)=c.id
            WHERE o.school_id=? AND o.session=?
            """);
        List<Object> args = new ArrayList<>(); args.add(schoolId); args.add(session);
        if (classId != null) { sql.append(" AND TRY_CONVERT(int,o.admissionClass)=?"); args.add(classId); }
        if (studentType != null && !studentType.isBlank()) { sql.append(" AND LOWER(COALESCE(o.studentType,''))=LOWER(?)"); args.add(studentType); }
        if (query != null && !query.isBlank()) {
            String q = "%"+query.trim()+"%";
            sql.append(" AND (o.registrationNO LIKE ? OR o.regPhone LIKE ? OR o.firstName LIKE ? OR o.lastName LIKE ?)");
            args.add(q); args.add(q); args.add(q); args.add(q);
        }
        if (status != null && !status.isBlank() && !"All".equalsIgnoreCase(status)) {
            if (statusReport) sql.append(" AND LOWER(COALESCE(o.AdmissionStatus,''))=LOWER(?)");
            else sql.append(" AND LOWER(COALESCE(o.formStatus,''))=LOWER(?)");
            args.add(status);
        }
        if (month != null && month > 0) { sql.append(" AND MONTH(TRY_CONVERT(date,o.createdate))=?"); args.add(month); }
        sql.append(" ORDER BY o.id DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    /**
     * Direct SQL equivalent of Report_UDISE_Promotion's Report_TopperReport
     * data source. The legacy report repeats the final total on each subject
     * row, so the student-level MAX values are used before returning rows for
     * the React section aggregator.
     */
    private List<Map<String, Object>> udisePromotion(String schoolId, Integer classId,
                                                       Integer sectionId, String session) {
        StringBuilder sql = new StringBuilder("""
            WITH student_totals AS (
                SELECT er.STU_ID AS studentId, er.CLASS_ID AS classId, er.sec_id AS sectionId,
                       er.session,
                       MAX(TRY_CONVERT(decimal(18,2), COALESCE(er.Grand_total, er.grand_pure_total))) AS totalMarks,
                       MAX(TRY_CONVERT(decimal(18,2), COALESCE(er.grand_max, er.grand_pure_max))) AS maxMarks,
                       MAX(NULLIF(LTRIM(RTRIM(COALESCE(er.grand_grade,''))), '')) AS grade
                FROM exam_reports er
                WHERE er.school_id=?
            """);
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        if (session != null && !session.isBlank()) { sql.append(" AND er.session=?"); args.add(session); }
        if (classId != null) { sql.append(" AND er.CLASS_ID=?"); args.add(classId); }
        if (sectionId != null) { sql.append(" AND er.sec_id=?"); args.add(sectionId); }
        sql.append("""
                GROUP BY er.STU_ID, er.CLASS_ID, er.sec_id, er.session
            )
            SELECT st.classId, st.sectionId,
                   COALESCE(c.class_name,'') AS className,
                   COALESCE(sc.section,'') AS sectionName,
                   COALESCE(c.class_name,'')+' ('+COALESCE(sc.section,'')+')' AS classname,
                   st.totalMarks, st.maxMarks,
                   CASE WHEN COALESCE(st.maxMarks,0)=0 THEN NULL
                        ELSE CAST(st.totalMarks*100.0/st.maxMarks AS decimal(18,2)) END AS overallper,
                   st.grade AS overallgrade
            FROM student_totals st
            LEFT JOIN class_name c ON c.school_id=? AND c.id=st.classId
            LEFT JOIN TB_Class sc ON sc.school_id=? AND sc.sec_id=st.sectionId
            ORDER BY st.classId, st.sectionId, st.studentId
            """);
        args.add(schoolId);
        args.add(schoolId);
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    private List<Map<String, Object>> registrationSearch(String schoolId, String query, Integer classId,
                                                         Integer sectionId, String status, String session,
                                                         boolean newStudentOnly, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT
                   sr.row_id AS studentId, sr.uid AS uid,
                   sr.form_no AS registrationNo, sr.class_id AS classId,
                   COALESCE(sm.sec_id, NULL) AS sectionId, sr.session AS session,
                   COALESCE(sm.status, sr.status) AS status,
                   sr.Registration_date AS registrationDate, sr.receipt_no AS receiptNo,
                   sr.mop AS mop, sr.reg_fee AS regFee,
                   sr.OnlineRegNo AS onlineRegNo, sr.approval_status AS approvalStatus,
                   sr.regi_type AS registrationType, sr.payment_uid AS paymentUid,
                   sr.transaction_no AS transactionNo, sr.amount AS paymentAmount,
                   sr.transaction_date AS paymentDate, sr.stream AS stream,
                   COALESCE(so.mobile,so.phone,'') AS registrationPhone,
                   COALESCE(so.father_name,'') AS registrationFatherName,
                   COALESCE(so.father_e_mail,'') AS registrationFatherEmail,
                   COALESCE(sm.Registration_NO, so.Registration_NO,'') AS admissionNo,
                   COALESCE(CAST(sm.RollNo AS varchar(50)),'') AS rollNo,
                   COALESCE(CAST(sm.FeeBookNo AS varchar(50)), CAST(so.FeeBookNo AS varchar(50)),'') AS feeBookNo,
                   sm.Admission_date AS admissionDate, sm.stutype AS studentType,
                   sm.isnew AS isNew,
                   COALESCE(c.class_name,'') AS className, COALESCE(sc.section,'') AS sectionName,
                   COALESCE(so.first_name, '') AS firstName,
                   COALESCE(so.mid_name, '') AS middleName,
                   COALESCE(so.last_name, '') AS lastName,
                   COALESCE(so.father_name, '') AS fatherName,
                   COALESCE(so.dob, '') AS dob,
                   COALESCE(so.gender, '') AS gender,
                   COALESCE(so.mobile, '') AS mobile,
                   COALESCE(so.phone, '') AS phone,
                   COALESCE(so.studentimage, '') AS studentImage,
                   COALESCE(so.Mother_name, '') AS motherName,
                   COALESCE(so.R_Address, '') AS address,
                   COALESCE(so.bloodgroup,'') AS bloodGroup,
                   COALESCE(so.quota,'') AS quota,
                   COALESCE(CAST(so.RTE AS varchar(10)),'') AS rte
            FROM student_reg sr
            LEFT JOIN student_other_info so ON so.uid=sr.uid AND so.school_id=sr.school_id
            OUTER APPLY (
                SELECT TOP 1 m.sec_id, m.status, m.Registration_NO, m.RollNo, m.FeeBookNo,
                       m.Admission_date, m.stutype, m.isnew
                FROM TB_StudentMaster m
                WHERE m.uid=sr.uid AND m.school_id=sr.school_id AND m.session=sr.session
                ORDER BY m.stu_id DESC
            ) sm
            LEFT JOIN class_name c ON c.id=sr.class_id AND c.school_id=sr.school_id
            LEFT JOIN TB_Class sc ON sc.sec_id=sm.sec_id AND sc.school_id=sr.school_id
            WHERE sr.school_id=?
            """);
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        if (query != null && !query.isBlank() && query.matches("\\d{4}-\\d{2}-\\d{2}")) {
            sql.append(" AND CONVERT(varchar(10), sr.Registration_date, 23)=?");
            args.add(query.trim());
        } else if (query != null && !query.isBlank()) {
            String value = "%" + query.trim() + "%";
            sql.append(" AND (sr.form_no LIKE ? OR so.first_name LIKE ? OR so.last_name LIKE ? OR so.mobile LIKE ?)");
            Collections.addAll(args, value, value, value, value);
        }
        if (classId != null) { sql.append(" AND sr.class_id=?"); args.add(classId); }
        if (sectionId != null) { sql.append(" AND sm.sec_id=?"); args.add(sectionId); }
        if (status != null && !status.isBlank()) { sql.append(" AND LOWER(sr.status)=LOWER(?)"); args.add(status); }
        if (session != null && !session.isBlank()) { sql.append(" AND sr.session=?"); args.add(session); }
        if (newStudentOnly) { sql.append(" AND COALESCE(sm.isnew,0)=1"); }
        if (fromDate != null && !fromDate.isBlank()) {
            sql.append(" AND CONVERT(date, sr.Registration_date)>=TRY_CONVERT(date, ?,23)");
            args.add(fromDate.trim());
        }
        if (toDate != null && !toDate.isBlank()) {
            sql.append(" AND CONVERT(date, sr.Registration_date)<=TRY_CONVERT(date, ?,23)");
            args.add(toDate.trim());
        }
        sql.append(" ORDER BY sr.row_id DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    private List<Map<String, Object>> strength(String schoolId, Integer classId,
                                                Integer sectionId, String status, String session) {
        StringBuilder sql = new StringBuilder("""
            SELECT sm.class_id AS classId, COALESCE(c.class_name,'') AS className,
                   sm.sec_id AS sectionId, COALESCE(sc.section,'') AS sectionName,
                   SUM(CASE WHEN LOWER(COALESCE(sm.stutype,'')) IN ('new','new admission','transfer','1') THEN 1 ELSE 0 END) AS newAdmission,
                   SUM(CASE WHEN LOWER(COALESCE(sm.stutype,'')) IN ('old','regular','2') THEN 1 ELSE 0 END) AS oldAdmission,
                   SUM(CASE WHEN LOWER(COALESCE(so.quota,''))='rte' OR COALESCE(so.RTE,0)=1 THEN 1 ELSE 0 END) AS rte,
                   COUNT(*) AS studentCount
            FROM TB_StudentMaster sm
            LEFT JOIN class_name c ON c.id=sm.class_id AND c.school_id=sm.school_id
            LEFT JOIN TB_Class sc ON sc.sec_id=sm.sec_id AND sc.school_id=sm.school_id
            LEFT JOIN student_reg sr ON sr.uid=sm.uid AND sr.school_id=sm.school_id
            WHERE sm.school_id=?
            """);
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        if (session != null && !session.isBlank()) { sql.append(" AND sm.session=?"); args.add(session); }
        if (status != null && !status.isBlank()) { sql.append(" AND LOWER(sm.status)=LOWER(?)"); args.add(status); }
        if (classId != null) { sql.append(" AND sm.class_id=?"); args.add(classId); }
        if (sectionId != null) { sql.append(" AND sm.sec_id=?"); args.add(sectionId); }
        sql.append(" GROUP BY sm.class_id,c.class_name,sm.sec_id,sc.section ORDER BY sm.class_id,sm.sec_id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    private List<Map<String, Object>> birthdayFaculty(String schoolId) {
        return jdbc.queryForList("""
            SELECT f.F_id AS facultyId,
                   LTRIM(RTRIM(CONCAT(COALESCE(f.First_name,''),' ',COALESCE(f.Mid_name,''),' ',COALESCE(f.Last_name,'')))) AS name,
                   f.DOB AS dob, COALESCE(f.Mobile,f.Phone,'') AS contact,
                   COALESCE(f.Department,'') AS className,
                   'Faculty' AS type
            FROM TB_FacultyMaster f
            WHERE f.school_id=? AND LOWER(COALESCE(CAST(f.status AS varchar(30)),'active')) NOT IN ('0','deleted','inactive')
            ORDER BY f.First_name, f.Last_name, f.F_id
            """, schoolId);
    }

    private Map<String, Object> summary(String schoolId, String session) {
        String sql = """
            SELECT COUNT(*) AS total,
                   SUM(CASE WHEN LOWER(status)='registered' THEN 1 ELSE 0 END) AS registered,
                   SUM(CASE WHEN LOWER(status)='withdrawn' THEN 1 ELSE 0 END) AS withdrawn
            FROM TB_StudentMaster
            WHERE school_id=?
            """;
        if (session == null || session.isBlank()) return jdbc.queryForMap(sql, schoolId);
        return jdbc.queryForMap(sql + " AND session=?", schoolId, session);
    }

    private String baseSelect() {
        return """
            SELECT
                   sm.stu_id AS studentId, sm.uid AS uid,
                   sm.registration_no AS registrationNo, sm.class_id AS classId,
                   sm.sec_id AS sectionId, sm.session AS session, sm.status AS status,
                   sm.stutype AS studentType, sm.isnew AS isNew, sm.House_id AS houseId,
                   sm.Admission_date AS admissionDate,
                   sm.Registration_date AS registrationDate,
                   sm.Withdrawn_date AS withdrawnDate, sm.Withdrawn_reason AS withdrawnReason,
                   sm.WithdrawnSession AS withdrawnSession,
                   COALESCE((SELECT TOP 1 tc.tc_no FROM transfer_certificate tc
                             WHERE tc.school_id=sm.school_id AND tc.stu_id=CAST(sm.stu_id AS varchar(100))
                               AND tc.session=sm.session ORDER BY tc.id DESC),'') AS tcNo,
                   so.Student_id AS studentLoginId, so.Password AS studentPwd,
                   so.Parent_id AS parentId, so.Parent_password AS parentPwd,
                   COALESCE(h.H_Name,'') AS house, COALESCE(h.H_Name,'') AS houseName,
                   COALESCE(c.class_name,'') AS className, COALESCE(sc.section,'') AS sectionName,
                   COALESCE(sc.classtype,'') AS classType,
                   COALESCE(so.first_name,'') AS firstName,
                   COALESCE(so.mid_name,'') AS middleName,
                   COALESCE(so.last_name,'') AS lastName,
                   COALESCE(so.father_name,'') AS fatherName,
                   so.dob AS dob, so.gender AS gender, so.mobile AS mobile,
                   so.phone AS phone,
                   CAST(sm.RollNo AS varchar(50)) AS rollNo,
                   CAST(sm.FeeBookNo AS varchar(50)) AS feeBookNo,
                   COALESCE(so.Mother_name,'') AS motherName,
                   COALESCE(so.R_Address,'') AS address,
                   COALESCE(so.student_aadhar,'') AS studentAadhar,
                   COALESCE(so.bloodgroup,'') AS bloodGroup,
                   COALESCE(so.childcat,'') AS category,
                   COALESCE(so.Religion,'') AS religion,
                   COALESCE(so.groups,'') AS studentGroup,
                   COALESCE(so.father_E_mail,'') AS fatherEmail,
                   COALESCE(so.mother_E_mail,'') AS motherEmail,
                   COALESCE(so.F_Qualification,'') AS fatherQualification,
                   COALESCE(so.M_Qualification,'') AS motherQualification,
                   COALESCE(so.F_Org_Add,'') AS fatherOrgAddress,
                   COALESCE(so.M_Org_Add,'') AS motherOrgAddress,
                   COALESCE(so.annual_income,'') AS fatherIncome,
                   COALESCE(so.family_income,'') AS motherIncome,
                   COALESCE(so.mother_aadhar,'') AS motherAadhar,
                   COALESCE(so.father_aadhar,'') AS fatherAadhar,
                   COALESCE(so.account_holder,'') AS accountHolder,
                   COALESCE(so.bank_name,'') AS bankName,
                   COALESCE(so.branch_name,'') AS branchName,
                   COALESCE(so.account_type,'') AS accountType,
                   COALESCE(so.bank_acc,'') AS accountNo,
                   COALESCE(so.ifsc_code,'') AS ifscCode,
                   COALESCE(so.F_Org_Add,'') AS fatherOfficeAddress,
                   COALESCE(so.M_Org_Add,'') AS motherOfficeAddress,
                   COALESCE(so.previous_school_name,'') AS previousSchool,
                   COALESCE(so.previous_school_aff,'') AS previousSchoolAffiliation,
                   COALESCE(so.Previous_Class,'') AS previousClass,
                   COALESCE(so.remark,'') AS remark,
                   COALESCE(so.quota,'') AS quota,
                   COALESCE(CAST(so.RTE AS varchar(10)),'') AS rte,
                   COALESCE(so.student_E_mail,'') AS studentEmail,
                   COALESCE(so.PEN_No,'') AS penNo, COALESCE(so.APAAR_ID,'') AS apaarId,
                   COALESCE(so.UDISE,'') AS udise, COALESCE(so.F_Ocp_Code,'') AS fatherOccupation,
                   COALESCE(so.M_Ocp_Code,'') AS motherOccupation,
                   COALESCE(so.F_whats_no,'') AS fatherWhatsapp, COALESCE(so.M_whats_no,'') AS motherWhatsapp,
                   COALESCE(so.att_date,'') AS attendanceDate,
                   COALESCE(so.ncc,'') AS ncc,
                   COALESCE(so.stream,'') AS stream,
                   COALESCE(sm.Optional_sub,'') AS optionalSubject,
                   COALESCE(so.SiblingId,'') AS siblingId,
                   COALESCE(so.sibling_admno,'') AS siblingAdmNo,
                   COALESCE(so.studentimage,'') AS studentImage,
                   COALESCE(so.fatherimage,'') AS fatherImage,
                   COALESCE(so.motherimage,'') AS motherImage,
                   COALESCE(so.District_id,0) AS cityId, COALESCE(so.State_id,0) AS stateId,
                   COALESCE(st.state_name,'') AS stateName, COALESCE(d.city_name,'') AS cityName,
                   COALESCE(so.BirthCertificate,'') AS birthCertificate,
                   COALESCE(so.TransferCertificate,'') AS transferCertificate,
                   COALESCE(so.Marksheet,'') AS marksheet, COALESCE(so.Migration,'') AS migration,
                   COALESCE(so.Aadhar_Card,'') AS aadharCard,
                   COALESCE(so.Cast_Certificate,'') AS castCertificate
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so
              ON so.uid=sm.uid AND so.school_id=sm.school_id
            LEFT JOIN class_name c
              ON c.id=sm.class_id AND c.school_id=sm.school_id
            LEFT JOIN TB_Class sc
              ON sc.sec_id=sm.sec_id AND sc.school_id=sm.school_id
            LEFT JOIN HouseMaster h
              ON h.House_id=sm.House_id AND h.school_id=sm.school_id
            LEFT JOIN state st ON st.state_id=so.State_id
            LEFT JOIN Districts d ON d.city_id=so.District_id
            WHERE sm.school_id=?
            """;
    }

    private void appendFilters(StringBuilder sql, List<Object> args, String query,
                               Integer classId, Integer sectionId, String status, String session) {
        if (query != null && !query.isBlank()) {
            sql.append("""
                AND (sm.registration_no LIKE ?
                     OR CAST(sm.stu_id AS varchar(50)) LIKE ?
                     OR so.first_name LIKE ? OR so.last_name LIKE ?
                     OR so.father_name LIKE ? OR so.mobile LIKE ?
                     OR c.class_name LIKE ? OR sc.section LIKE ?)
                """);
            String value = "%" + query.trim() + "%";
            Collections.addAll(args, value, value, value, value, value, value, value, value);
        }
        if (classId != null) { sql.append(" AND sm.class_id=?"); args.add(classId); }
        if (sectionId != null) { sql.append(" AND sm.sec_id=?"); args.add(sectionId); }
        if (status != null && !status.isBlank()) { sql.append(" AND LOWER(sm.status)=LOWER(?)"); args.add(status); }
        if (session != null && !session.isBlank()) { sql.append(" AND sm.session=?"); args.add(session); }
    }
}
