package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            SELECT id AS classId, class_name AS className, class_group AS classGroup
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
        String yearEnd = session.length() >= 2 ? session.substring(session.length() - 2) : "0";
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
        out.put("quota", jdbc.queryForList("SELECT DISTINCT id, quota AS name FROM quota WHERE created_by='ADMIN' ORDER BY id"));
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
                   COALESCE(so.quota, sr.quota,'') AS quota,
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
                                            String fromDate, String toDate) {
        String normalized = type == null ? "student-list" : type.trim().toLowerCase();
        if (normalized.equals("registration") || normalized.equals("registration-details")
            || normalized.equals("new-student")) {
            return registrationSearch(schoolId, query, classId, sectionId, status, session,
                normalized.equals("new-student"), fromDate, toDate);
        }
        if (normalized.equals("strength") || normalized.equals("classwise-strength")
            || normalized.equals("all-student-strength")) {
            return strength(schoolId, classId, sectionId, status, session);
        }
        if (normalized.equals("birthday") && "faculty".equalsIgnoreCase(studentType)) {
            return birthdayFaculty(schoolId);
        }
        if (normalized.equals("udise-promotion")) {
            return udisePromotion(schoolId, classId, sectionId, session);
        }

        StringBuilder sql = new StringBuilder(baseSelect());
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        appendFilters(sql, args, query, classId, sectionId, status, session);

        switch (normalized) {
            case "admitted", "new-student" -> sql.append(" AND LOWER(sm.status)='registered'");
            case "withdrawn" -> sql.append(" AND LOWER(sm.status)='withdrawn'");
            case "withdrawn-management" -> { /* old update page loads both Registered and Withdrawn */ }
            case "birthday" -> {
                if (month != null && month >= 1 && month <= 12) {
                    sql.append(" AND MONTH(so.dob)=?");
                    args.add(month);
                }
            }
            case "student-type", "house", "sibling", "registration", "registration-details",
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
            SELECT TOP (1000)
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
                   sr.mobile AS registrationPhone, sr.father_name AS registrationFatherName,
                   sr.father_e_mail AS registrationFatherEmail,
                   COALESCE(sm.Registration_NO, so.Registration_NO,'') AS admissionNo,
                   COALESCE(CAST(sm.RollNo AS varchar(50)),'') AS rollNo,
                   COALESCE(CAST(sm.FeeBookNo AS varchar(50)), CAST(so.FeeBookNo AS varchar(50)),'') AS feeBookNo,
                   sm.Admission_date AS admissionDate, sm.stutype AS studentType,
                   sm.isnew AS isNew,
                   COALESCE(c.class_name,'') AS className, COALESCE(sc.section,'') AS sectionName,
                   COALESCE(so.first_name, sr.first_name, '') AS firstName,
                   COALESCE(so.mid_name, sr.mid_name, '') AS middleName,
                   COALESCE(so.last_name, sr.last_name, '') AS lastName,
                   COALESCE(so.father_name, sr.father_name, '') AS fatherName,
                   COALESCE(so.dob, sr.dob) AS dob,
                   COALESCE(so.gender, sr.gender) AS gender,
                   COALESCE(so.mobile, sr.mobile) AS mobile,
                   COALESCE(so.phone, sr.phone) AS phone,
                   COALESCE(so.studentimage, sr.studentimage) AS studentImage,
                   COALESCE(so.Mother_name, sr.mother_name,'') AS motherName,
                   COALESCE(so.R_Address, sr.r_address,'') AS address,
                   COALESCE(so.bloodgroup,'') AS bloodGroup,
                   COALESCE(so.quota, sr.quota,'') AS quota,
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
            sql.append(" AND (sr.form_no LIKE ? OR sr.first_name LIKE ? OR sr.last_name LIKE ? OR sr.mobile LIKE ?)");
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
                   SUM(CASE WHEN LOWER(COALESCE(sr.quota,''))='rte' THEN 1 ELSE 0 END) AS rte,
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
            SELECT TOP (1000)
                   sm.stu_id AS studentId, sm.uid AS uid,
                   sm.registration_no AS registrationNo, sm.class_id AS classId,
                   sm.sec_id AS sectionId, sm.session AS session, sm.status AS status,
                   sm.stutype AS studentType, sm.House_id AS houseId,
                   sm.Admission_date AS admissionDate,
                   so.Student_id AS studentLoginId, so.Password AS studentPwd,
                   so.Parent_id AS parentId, so.Parent_password AS parentPwd,
                   COALESCE(h.H_Name,'') AS house, COALESCE(h.H_Name,'') AS houseName,
                   COALESCE(c.class_name,'') AS className, COALESCE(sc.section,'') AS sectionName,
                   COALESCE(so.first_name,'') AS firstName,
                   COALESCE(so.mid_name,'') AS middleName,
                   COALESCE(so.last_name,'') AS lastName,
                   COALESCE(so.father_name,'') AS fatherName,
                   so.dob AS dob, so.gender AS gender, so.mobile AS mobile,
                   so.phone AS phone, so.studentimage AS studentImage,
                   CAST(sm.RollNo AS varchar(50)) AS rollNo,
                   CAST(sm.FeeBookNo AS varchar(50)) AS feeBookNo,
                   COALESCE(so.Mother_name,'') AS motherName,
                   COALESCE(so.R_Address,'') AS address,
                   COALESCE(so.student_aadhar,'') AS studentAadhar,
                   COALESCE(so.bloodgroup,'') AS bloodGroup,
                   COALESCE(so.childcat,'') AS category,
                   COALESCE(so.Religion,'') AS religion,
                   COALESCE(so.groups,'') AS studentGroup,
                   COALESCE(so.F_Qualification,'') AS fatherQualification,
                   COALESCE(so.M_Qualification,'') AS motherQualification,
                   COALESCE(so.father_E_mail,'') AS fatherEmail,
                   COALESCE(so.mother_E_mail,'') AS motherEmail,
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
