package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RegistrationRedirectionRepository {
    private final JdbcTemplate jdbc;

    @Transactional
    public Map<String, Object> findOrCreate(String schoolId, String staffUid, String session, String phone) {
        String selectedSession = text(session);
        String mobile = text(phone);
        if (selectedSession.isBlank()) throw new IllegalArgumentException("Session is required");
        if (mobile.isBlank()) throw new IllegalArgumentException("Mobile number is required");

        var existing = jdbc.queryForList("""
            SELECT TOP 1 uid, formNo, regPhone, school_id, session, registrationNO
            FROM onlineStudentRegistration
            WHERE school_id=? AND regPhone=? AND session=? AND IsPrimary=1
            ORDER BY uid
            """, schoolId, mobile, selectedSession);
        if (!existing.isEmpty()) return existing.get(0);

        Integer uid = jdbc.queryForObject(
            "SELECT ISNULL(MAX(uid),0)+1 FROM onlineStudentRegistration WHERE school_id=?",
            Integer.class, schoolId);
        Integer formNo = jdbc.queryForObject("""
            SELECT ISNULL(MAX(formNo),0)+1
            FROM onlineStudentRegistration
            WHERE school_id=? AND session=?
            """, Integer.class, schoolId, selectedSession);
        String newForm = String.format("%04d", formNo == null ? 1 : formNo);
        String shortSession = shortSession(selectedSession);
        String prefix = jdbc.query(String.valueOf("SELECT TOP 1 COALESCE(schoolPrefix,'') FROM configuration WHERE school_id=?"),
            ps -> ps.setString(1, schoolId), rs -> rs.next() ? rs.getString(1) : "");
        String registrationNo = (prefix == null || prefix.isBlank() ? "" : prefix.trim() + "/") + newForm + "/" + shortSession;

        jdbc.update("""
            INSERT INTO onlineStudentRegistration
              (uid, formNo, regPhone, createdate, formGeneratePlatform, school_id,
               formStatus, session, registrationNO, IsPrimary)
            VALUES (?, ?, ?, GETDATE(), 'online', ?, 'started', ?, ?, 1)
            """, uid, formNo, mobile, schoolId, selectedSession, registrationNo);
        jdbc.update("""
            INSERT INTO onlineRegistrationFormSecStatus(regPhone, studentID, schoolID, session, uid)
            VALUES (?, '', ?, ?, ?)
            """, mobile, schoolId, selectedSession, uid);
        return Map.of("uid", uid, "formNo", formNo, "regPhone", mobile,
            "school_id", schoolId, "session", selectedSession, "registrationNO", registrationNo);
    }

    private static String shortSession(String session) {
        String compact = session.replace(" ", "");
        if (compact.length() >= 9 && compact.charAt(4) == '-') return compact.substring(2, 4) + "-" + compact.substring(7, 9);
        return compact.length() >= 4 ? compact.substring(Math.max(0, compact.length() - 4)) : compact;
    }

    private static String text(String value) { return value == null ? "" : value.trim(); }
}
