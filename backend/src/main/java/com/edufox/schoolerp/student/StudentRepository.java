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

    public List<Map<String, Object>> report(String schoolId, String type, String query,
                                            Integer classId, Integer sectionId, String status,
                                            String session, Integer month) {
        String normalized = type == null ? "student-list" : type.trim().toLowerCase();
        if (normalized.equals("registration") || normalized.equals("registration-details")
            || normalized.equals("new-student")) {
            return registrationSearch(schoolId, query, classId, status, session);
        }
        if (normalized.equals("strength") || normalized.equals("classwise-strength")
            || normalized.equals("all-student-strength")) {
            return strength(schoolId, classId, sectionId, status, session);
        }

        StringBuilder sql = new StringBuilder(baseSelect());
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        appendFilters(sql, args, query, classId, sectionId, status, session);

        switch (normalized) {
            case "admitted", "new-student" -> sql.append(" AND LOWER(sm.status)='registered'");
            case "withdrawn" -> sql.append(" AND LOWER(sm.status)='withdrawn'");
            case "birthday" -> {
                if (month != null && month >= 1 && month <= 12) {
                    sql.append(" AND MONTH(so.dob)=?");
                    args.add(month);
                }
            }
            case "student-type", "house", "sibling", "registration", "registration-details",
                 "student-record", "student-list", "id-password" -> {
                // These pages share the same safe student projection until their
                // page-specific legacy procedure is migrated.
            }
            default -> throw new IllegalArgumentException("Unsupported student report type");
        }

        sql.append(" ORDER BY COALESCE(so.first_name,''), COALESCE(so.last_name,''), sm.stu_id");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    private List<Map<String, Object>> registrationSearch(String schoolId, String query, Integer classId, String status, String session) {
        StringBuilder sql = new StringBuilder("""
            SELECT TOP (1000)
                   sr.row_id AS studentId, sr.uid AS uid,
                   sr.form_no AS registrationNo, sr.class_id AS classId,
                   NULL AS sectionId, sr.session AS session, sr.status AS status,
                   sr.Registration_date AS registrationDate, sr.receipt_no AS receiptNo,
                   sr.mop AS mop, COALESCE(so.Registration_NO,'') AS admissionNo,
                   COALESCE(c.class_name,'') AS className, '' AS sectionName,
                   COALESCE(so.first_name, sr.first_name, '') AS firstName,
                   COALESCE(so.mid_name, sr.mid_name, '') AS middleName,
                   COALESCE(so.last_name, sr.last_name, '') AS lastName,
                   COALESCE(so.father_name, sr.father_name, '') AS fatherName,
                   COALESCE(so.dob, sr.dob) AS dob,
                   COALESCE(so.gender, sr.gender) AS gender,
                   COALESCE(so.mobile, sr.mobile) AS mobile,
                   COALESCE(so.phone, sr.phone) AS phone,
                   COALESCE(so.studentimage, sr.studentimage) AS studentImage
            FROM student_reg sr
            LEFT JOIN student_other_info so ON so.uid=sr.uid AND so.school_id=sr.school_id
            LEFT JOIN class_name c ON c.id=sr.class_id AND c.school_id=sr.school_id
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
        if (status != null && !status.isBlank()) { sql.append(" AND LOWER(sr.status)=LOWER(?)"); args.add(status); }
        if (session != null && !session.isBlank()) { sql.append(" AND sr.session=?"); args.add(session); }
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
                   so.Student_id AS studentLoginId, so.Password AS studentPwd,
                   so.Parent_id AS parentId, so.Parent_password AS parentPwd,
                   COALESCE(h.H_Name,'') AS house, COALESCE(h.H_Name,'') AS houseName,
                   COALESCE(c.class_name,'') AS className, COALESCE(sc.section,'') AS sectionName,
                   COALESCE(so.first_name,'') AS firstName,
                   COALESCE(so.mid_name,'') AS middleName,
                   COALESCE(so.last_name,'') AS lastName,
                   COALESCE(so.father_name,'') AS fatherName,
                   so.dob AS dob, so.gender AS gender, so.mobile AS mobile,
                   so.phone AS phone, so.studentimage AS studentImage
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so
              ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id
            LEFT JOIN class_name c
              ON c.id=sm.class_id AND c.school_id=sm.school_id
            LEFT JOIN TB_Class sc
              ON sc.sec_id=sm.sec_id AND sc.school_id=sm.school_id
            LEFT JOIN HouseMaster h
              ON h.House_id=sm.House_id AND h.school_id=sm.school_id
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
