package com.edufox.schoolerp.common.persistence;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Shared session selection used by page workflows that accept an optional
 * session. The ordering intentionally matches the legacy application: the
 * configured Default session wins, then the latest session id.
 */
public final class SessionResolver {
    private SessionResolver() {
    }

    public static String resolve(JdbcTemplate jdbc, Object requested, String schoolId) {
        String value = requested == null ? "" : String.valueOf(requested).trim();
        if (!value.isBlank() && !"0".equals(value)) {
            return value;
        }

        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT TOP 1 session
            FROM TB_Session
            WHERE school_id=?
            ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END, id DESC
            """, schoolId);
        return rows.isEmpty() || rows.get(0).get("session") == null
            ? ""
            : String.valueOf(rows.get(0).get("session")).trim();
    }
}
