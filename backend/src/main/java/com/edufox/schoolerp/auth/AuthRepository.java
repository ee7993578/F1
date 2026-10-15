package com.edufox.schoolerp.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Read-only replacement for the legacy login procedures.
 * It preserves the legacy tables/columns and never writes login, OTP, session,
 * or audit rows.
 */
@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final JdbcTemplate jdbc;

    public AuthUser findUser(String id, String password, String role, String type) {
        try {
            return switch (role.toLowerCase(Locale.ROOT)) {
                case "faculty" -> faculty(id, password, type);
                case "student" -> student(id, password, type, false);
                case "parent" -> student(id, password, type, true);
                default -> null;
            };
        } catch (EmptyResultDataAccessException ignored) {
            return null;
        }
    }

    private AuthUser faculty(String id, String password, String type) {
        String identityColumn = "Email".equals(type) ? "f.E_mail" : "f.Mobile";
        Map<String, Object> row = jdbc.queryForMap(("""
            SELECT f.school_id, sm.schoolname, sm.campusid,
                   CONCAT(COALESCE(f.first_name,''),' ',COALESCE(f.mid_name,''),' ',COALESCE(f.last_name,'')) AS username,
                   f.staff_u_id, f.image_path,
                   CASE WHEN COALESCE(sm.logo_path,'')='' THEN ''
                        WHEN LOWER(sm.logo_path) LIKE 'http%%' THEN sm.logo_path
                        ELSE COALESCE(sm.web_address,'') + sm.logo_path END AS logo_path,
                   COALESCE(c.colorScheme,'') AS colorScheme,
                   COALESCE(c.otp_based,0) AS otp_based,
                   COALESCE(f.otp_bases_access,0) AS otp_bases_access
            FROM TB_FacultyMaster f
            JOIN schoolmaster sm ON sm.school_id=f.school_id AND sm.status=1
            LEFT JOIN configuration c ON c.school_id=f.school_id
            WHERE %s=? AND f.Password=? AND f.status=1
            """).formatted(identityColumn), id, password);

        String schoolId = value(row, "school_id");
        String staffUid = value(row, "staff_u_id");
        // The legacy procedure requires both switches before asking for OTP.
        boolean otpRequired = "1".equals(value(row, "otp_based"))
            && "1".equals(value(row, "otp_bases_access"));
        return new AuthUser(schoolId, value(row, "schoolname"), value(row, "campusid"),
            cleanName(value(row, "username")), staffUid, value(row, "image_path"),
            value(row, "logo_path"), value(row, "colorScheme"), "F",
            dashboard(schoolId, staffUid), "", type, otpRequired);
    }

    private AuthUser student(String id, String password, String type, boolean parent) {
        String identityColumn = "Email".equals(type)
            ? (parent ? "f.Parent_id" : "f.Student_id")
            : "f.Mobile";
        String passwordColumn = parent ? "f.Parent_password" : "f.Password";

        Map<String, Object> row = jdbc.queryForMap(("""
            SELECT f.school_id, sm.schoolname, sm.campusid,
                   CONCAT(COALESCE(f.First_name,''),' ',COALESCE(f.Mid_name,''),' ',COALESCE(f.Last_name,'')) AS username,
                   s.stu_id AS staff_u_id, f.studentimage AS image_path,
                   CASE WHEN COALESCE(sm.logo_path,'')='' THEN ''
                        WHEN LOWER(sm.logo_path) LIKE 'http%%' THEN sm.logo_path
                        ELSE COALESCE(sm.web_address,'') + sm.logo_path END AS logo_path,
                   COALESCE(c.colorScheme,'') AS colorScheme
            FROM student_other_info f
            JOIN TB_StudentMaster s ON s.registration_no=f.registration_no AND s.school_id=f.school_id
            JOIN schoolmaster sm ON sm.school_id=f.school_id AND sm.status=1
            LEFT JOIN configuration c ON c.school_id=f.school_id
            WHERE %s=? AND %s=? AND f.login_status=1
              AND s.session=(SELECT session FROM TB_Session WHERE school_id=f.school_id AND status='Default')
            """).formatted(identityColumn, passwordColumn), id, password);

        return new AuthUser(value(row, "school_id"), value(row, "schoolname"),
            value(row, "campusid"), cleanName(value(row, "username")),
            value(row, "staff_u_id"), value(row, "image_path"), value(row, "logo_path"),
            value(row, "colorScheme"), parent ? "P" : "S", "0", "", type, false);
    }

    private String dashboard(String schoolId, String staffUid) {
        List<String> designationIds = jdbc.query(
            "SELECT Desg_id FROM desig_mapping WHERE school_id=? AND Staff_U_id=? ORDER BY id",
            (rs, ignored) -> rs.getString(1), schoolId, staffUid);
        if (designationIds.stream().anyMatch("28"::equals)) return "28";
        if (designationIds.stream().anyMatch("27"::equals)) return "27";
        return "F";
    }

    private static String cleanName(String name) {
        return name.replaceAll("\\s+", " ").trim();
    }

    private static String value(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) value = row.get(key.toUpperCase(Locale.ROOT));
        return value == null ? "" : String.valueOf(value);
    }
}
