package com.edufox.schoolerp.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> dashboard(String school, String type, String requestedSession, int days) {
        String current = requestedSession == null || requestedSession.isBlank() ? latestSession(school) : requestedSession.trim();
        String previous = previousSession(school, current);
        Map<String, Object> result = switch (normalize(type)) {
            case "attendance", "admindashboardattendance" -> attendance(school, current, days);
            case "fees", "admindashboardfees" -> fees(school, current);
            case "marks", "admindashboardmarks" -> marks(school);
            case "faculty", "facultyattendancedashboard" -> facultyAttendance(school, current, days);
            case "library", "librarydashboard" -> library(school);
            case "management", "managementdashboard" -> management(school, current);
            default -> academic(school, current, previous, days);
        };
        result.put("session", current);
        result.put("previousSession", previous);
        return result;
    }

    private Map<String, Object> academic(String school, String current, String previous, int days) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", one("""
            SELECT COUNT(*) total,
                   SUM(CASE WHEN LOWER(COALESCE(so.gender,'')) IN ('m','male','boy') THEN 1 ELSE 0 END) male,
                   SUM(CASE WHEN LOWER(COALESCE(so.gender,'')) IN ('f','female','girl') THEN 1 ELSE 0 END) female
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id
            WHERE sm.school_id=? AND LOWER(COALESCE(sm.status,'')) <> 'withdrawn'
            """, school));
        result.put("admissions", safe("""
            SELECT CONVERT(varchar(10), sr.Registration_date, 23) label, COUNT(*) value
            FROM student_reg sr
            WHERE sr.school_id=? AND sr.Registration_date >= DATEADD(day, ?, CAST(GETDATE() AS date))
            GROUP BY CONVERT(varchar(10), sr.Registration_date, 23) ORDER BY label
            """, school, -days));
        result.put("genderComparison", safe("""
            SELECT CASE WHEN LOWER(COALESCE(so.gender,'')) IN ('m','male','boy') THEN 'Male' ELSE 'Female' END label,
                   SUM(CASE WHEN sm.session=? THEN 1 ELSE 0 END) currentValue,
                   SUM(CASE WHEN sm.session=? THEN 1 ELSE 0 END) previousValue
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id
            WHERE sm.school_id=?
            GROUP BY CASE WHEN LOWER(COALESCE(so.gender,'')) IN ('m','male','boy') THEN 'Male' ELSE 'Female' END
            ORDER BY label
            """, current, previous, school));
        result.put("strengthByClass", safe("""
            SELECT COALESCE(c.class_name,CONVERT(varchar(50),sm.class_id)) label,
                   SUM(CASE WHEN sm.session=? THEN 1 ELSE 0 END) currentValue,
                   SUM(CASE WHEN sm.session=? THEN 1 ELSE 0 END) previousValue
            FROM TB_StudentMaster sm
            LEFT JOIN class_name c ON c.id=sm.class_id AND c.school_id=sm.school_id
            WHERE sm.school_id=? GROUP BY c.class_name,sm.class_id ORDER BY sm.class_id
            """, current, previous, school));
        result.put("religion", groupedStudentField(school, "religion"));
        result.put("categories", groupedStudentField(school, "category"));
        result.put("withdrawn", safe("SELECT COALESCE(sm.status,'Unknown') label, COUNT(*) value FROM TB_StudentMaster sm WHERE sm.school_id=? AND LOWER(COALESCE(sm.status,''))='withdrawn' GROUP BY sm.status ORDER BY value DESC", school));
        result.put("tcByMonth", tcByMonth(school));
        return result;
    }

    private Map<String, Object> management(String school, String session) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", one("""
            SELECT SUM(CASE WHEN LOWER(COALESCE(status,''))='registered' THEN 1 ELSE 0 END) registered,
                   SUM(CASE WHEN LOWER(COALESCE(status,''))='withdrawn' THEN 1 ELSE 0 END) withdrawn,
                   COUNT(*) total FROM TB_StudentMaster WHERE school_id=? AND (?='' OR session=?)
            """, school, session, session));
        result.put("faculty", safe("SELECT COUNT(*) value FROM tb_facultymaster WHERE school_id=?", school));
        result.put("holidays", firstWorkingTable(school, "holiday", "holiday_name", "fromdate", "todate"));
        result.put("circulars", firstWorkingTable(school, "circular", "circularname", "cir_date", "cir_files"));
        result.put("fees", feeSummary(school));
        return result;
    }

    private Map<String, Object> attendance(String school, String session, int days) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", studentSummary(school, session));
        TableInfo table = findTable("attend", Set.of("school_id", "schoolid"));
        String date = table == null ? null : table.column("attendance_date", "att_date", "date_of_attendance", "attendancedate", "date");
        String status = table == null ? null : table.column("attendance", "att_status", "present_absent", "is_present", "status");
        if (table == null || date == null || status == null) {
            result.put("dateWise", List.of()); result.put("classWise", List.of()); result.put("sectionWise", List.of()); result.put("monthly", List.of()); result.put("authentication", List.of());
        } else {
            String present = "CASE WHEN LOWER(CONVERT(varchar(30)," + qi(status) + ")) IN ('p','present','1','true') THEN 1 ELSE 0 END";
            String where = whereFor(table); Object[] args = argsFor(table, school);
            result.put("dateWise", safe("SELECT CONVERT(varchar(10)," + qi(date) + ",23) label, SUM(" + present + ") present, COUNT(*)-SUM(" + present + ") absent FROM " + table.name() + where + andOrWhere(where) + qi(date) + ">=DATEADD(day,?,CAST(GETDATE() AS date)) GROUP BY CONVERT(varchar(10)," + qi(date) + ",23) ORDER BY label", append(args, -days)));
            String classCol = table.column("class_id", "classid", "class");
            result.put("classWise", classCol == null ? List.of() : safe("SELECT CONVERT(varchar(100)," + qi(classCol) + ") label, SUM(" + present + ") present, COUNT(*)-SUM(" + present + ") absent FROM " + table.name() + where + " GROUP BY " + qi(classCol) + " ORDER BY label", args));
            String sectionCol = table.column("sec_id", "section_id", "sectionid", "section");
            result.put("sectionWise", sectionCol == null ? List.of() : safe("SELECT CONVERT(varchar(100)," + qi(sectionCol) + ") label, SUM(" + present + ") present, COUNT(*)-SUM(" + present + ") absent FROM " + table.name() + where + " GROUP BY " + qi(sectionCol) + " ORDER BY label", args));
            result.put("monthly", safe("SELECT LEFT(CONVERT(varchar(10)," + qi(date) + ",23),7) label, SUM(" + present + ") present, COUNT(*)-SUM(" + present + ") absent FROM " + table.name() + where + " GROUP BY LEFT(CONVERT(varchar(10)," + qi(date) + ",23),7) ORDER BY label", args));
            result.put("authentication", safe("SELECT COALESCE(CONVERT(varchar(100)," + qi(status) + "),'Unknown') label, COUNT(*) value FROM " + table.name() + where + " GROUP BY " + qi(status) + " ORDER BY value DESC", args));
        }
        result.put("holidays", firstWorkingTable(school, "holiday", "holiday_name", "fromdate", "todate"));
        return result;
    }

    private Map<String, Object> facultyAttendance(String school, String session, int days) {
        Map<String, Object> result = attendance(school, session, days);
        result.put("staff", result.getOrDefault("dateWise", List.of()));
        return result;
    }

    private Map<String, Object> fees(String school, String session) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", feeSummary(school));
        TableInfo table = findTable("fee", Set.of("school_id", "schoolid"));
        String amount = table == null ? null : table.column("paid_amount", "receive_amount", "received_amount", "deposit_amt", "fee_amount", "total_amount", "amount");
        String date = table == null ? null : table.column("payment_date", "deposit_date", "receipt_date", "transaction_date", "date");
        String mode = table == null ? null : table.column("payment_mode", "mop", "pay_mode", "mode");
        if (table == null || amount == null) {
            result.put("byMonth", List.of()); result.put("byMode", List.of()); result.put("defaulters", List.of());
        } else {
            String where = whereFor(table); Object[] args = argsFor(table, school);
            result.put("byMonth", date == null ? List.of() : safe("SELECT LEFT(CONVERT(varchar(10)," + qi(date) + ",23),7) label, SUM(TRY_CONVERT(decimal(18,2)," + qi(amount) + ")) value FROM " + table.name() + where + " GROUP BY LEFT(CONVERT(varchar(10)," + qi(date) + ",23),7) ORDER BY label", args));
            result.put("byMode", mode == null ? List.of() : safe("SELECT COALESCE(CONVERT(varchar(100)," + qi(mode) + "),'Unknown') label, SUM(TRY_CONVERT(decimal(18,2)," + qi(amount) + ")) value FROM " + table.name() + where + " GROUP BY " + qi(mode) + " ORDER BY value DESC", args));
            result.put("defaulters", safe("SELECT TOP (100) sm.class_id label, COUNT(*) value FROM TB_StudentMaster sm WHERE sm.school_id=? GROUP BY sm.class_id ORDER BY value DESC", school));
        }
        return result;
    }

    private Map<String, Object> marks(String school) {
        Map<String, Object> result = new LinkedHashMap<>();
        TableInfo table = findTable("mark", Set.of("school_id", "schoolid"));
        result.put("summary", table == null ? Map.of() : one("SELECT COUNT(*) total FROM " + table.name() + whereFor(table), argsFor(table, school)));
        result.put("byClass", groupedTable(table, school, "class_id", "classid", "class"));
        result.put("byStatus", groupedTable(table, school, "status", "entry_status", "mark_status"));
        return result;
    }

    private Map<String, Object> library(String school) {
        TableInfo books = findTable("book", Set.of("school_id", "schoolid"));
        TableInfo issues = findTable("issue", Set.of("school_id", "schoolid"));
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("books", countTable(books, school));
        summary.put("issuedToday", countDateTable(issues, school, "issue_date", "issued_date", "date"));
        summary.put("returnsDue", countDateTable(issues, school, "due_date", "return_date"));
        summary.put("fine", sumTable(issues, school, "fine", "fine_amount"));
        result.put("summary", summary);
        result.put("circulation", groupedTable(issues, school, "member_type", "membertype", "type"));
        result.put("fineByMonth", groupedDateTable(issues, school, "fine", "fine_amount", "amount", "date", "issue_date", "issued_date"));
        result.put("recentIssues", safeTableRows(issues, school));
        result.put("schedule", firstWorkingTable(school, "library", "period", "times", "classes"));
        return result;
    }

    private Map<String, Object> studentSummary(String school, String session) {
        Map<String, Object> result = one("""
            SELECT COUNT(*) total,
                   SUM(CASE WHEN LOWER(COALESCE(so.gender,'')) IN ('m','male','boy') THEN 1 ELSE 0 END) male,
                   SUM(CASE WHEN LOWER(COALESCE(so.gender,'')) IN ('f','female','girl') THEN 1 ELSE 0 END) female
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id
            WHERE sm.school_id=? AND (?='' OR sm.session=?)
            """, school, session, session);
        result.putIfAbsent("present", 0); result.putIfAbsent("absent", 0);
        return result;
    }

    private Map<String, Object> feeSummary(String school) {
        TableInfo table = findTable("fee", Set.of("school_id", "schoolid"));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("payable", sumTable(table, school, "payable", "total_amount", "fee_amount"));
        result.put("received", sumTable(table, school, "receive_amount", "received_amount", "paid_amount", "amount"));
        result.put("balance", sumTable(table, school, "balance", "due_amount", "outstanding"));
        return result;
    }

    private List<Map<String, Object>> groupedStudentField(String school, String field) {
        return safe("""
            SELECT COALESCE(CONVERT(varchar(100),so.%s),'Unknown') label, COUNT(*) value
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.registration_no=sm.registration_no AND so.school_id=sm.school_id
            WHERE sm.school_id=? GROUP BY so.%s ORDER BY value DESC
            """.formatted(qi(field), qi(field)), school);
    }

    private List<Map<String, Object>> tcByMonth(String school) {
        TableInfo table = findTable("withdraw", Set.of("school_id", "schoolid"));
        if (table == null) return List.of();
        String date = table.column("withdraw_date", "tc_date", "issue_date", "date");
        if (date == null) return List.of();
        return safe("SELECT LEFT(CONVERT(varchar(10)," + qi(date) + ",23),7) label, COUNT(*) value FROM " + table.name() + whereFor(table) + " GROUP BY LEFT(CONVERT(varchar(10)," + qi(date) + ",23),7) ORDER BY label", argsFor(table, school));
    }

    private List<Map<String, Object>> firstWorkingTable(String school, String token, String... wanted) {
        TableInfo table = findTable(token, Set.of("school_id", "schoolid"));
        if (table == null) return List.of();
        String selected = Arrays.stream(wanted)
            .filter(c -> table.columns.contains(c.toLowerCase(Locale.ROOT)))
            .map(DashboardRepository::qi)
            .reduce((a, b) -> a + "," + b).orElse("*");
        return safe("SELECT TOP (100) " + selected + " FROM " + table.name() + whereFor(table), argsFor(table, school));
    }

    private List<Map<String, Object>> groupedTable(TableInfo table, String school, String... candidates) {
        if (table == null) return List.of();
        String column = table.column(candidates);
        return column == null ? List.of() : safe("SELECT COALESCE(CONVERT(varchar(100)," + qi(column) + "),'Unknown') label, COUNT(*) value FROM " + table.name() + whereFor(table) + " GROUP BY " + qi(column) + " ORDER BY value DESC", argsFor(table, school));
    }

    private List<Map<String, Object>> groupedDateTable(TableInfo table, String school, String value1, String value2, String... dates) {
        if (table == null) return List.of();
        String value = table.column(value1, value2, "amount");
        String date = table.column(dates);
        return value == null || date == null ? List.of() : safe("SELECT LEFT(CONVERT(varchar(10)," + qi(date) + ",23),7) label, SUM(TRY_CONVERT(decimal(18,2)," + qi(value) + ")) value FROM " + table.name() + whereFor(table) + " GROUP BY LEFT(CONVERT(varchar(10)," + qi(date) + ",23),7) ORDER BY label", argsFor(table, school));
    }

    private List<Map<String, Object>> safeTableRows(TableInfo table, String school) {
        return table == null ? List.of() : safe("SELECT TOP (100) * FROM " + table.name() + whereFor(table), argsFor(table, school));
    }

    private int countTable(TableInfo table, String school) {
        if (table == null) return 0;
        return number(one("SELECT COUNT(*) value FROM " + table.name() + whereFor(table), argsFor(table, school)).get("value"));
    }

    private int countDateTable(TableInfo table, String school, String... candidates) {
        if (table == null) return 0;
        String date = table.column(candidates);
        if (date == null) return 0;
        String where = whereFor(table);
        return number(one("SELECT COUNT(*) value FROM " + table.name() + where + andOrWhere(where) + "CAST(" + qi(date) + " AS date)=CAST(GETDATE() AS date)", argsFor(table, school)).get("value"));
    }

    private Object sumTable(TableInfo table, String school, String... candidates) {
        if (table == null) return 0;
        String amount = table.column(candidates);
        if (amount == null) return 0;
        return one("SELECT COALESCE(SUM(TRY_CONVERT(decimal(18,2)," + qi(amount) + ")),0) value FROM " + table.name() + whereFor(table), argsFor(table, school)).getOrDefault("value", 0);
    }

    private TableInfo findTable(String token, Set<String> required) {
        List<Map<String, Object>> rows = safe("SELECT TABLE_SCHEMA tableSchema, TABLE_NAME tableName, COLUMN_NAME columnName FROM INFORMATION_SCHEMA.COLUMNS WHERE LOWER(TABLE_NAME) LIKE ? ORDER BY TABLE_SCHEMA,TABLE_NAME,ORDINAL_POSITION", "%" + token.toLowerCase(Locale.ROOT) + "%");
        Map<String, TableInfo> tables = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String schema = String.valueOf(row.get("tableSchema"));
            String name = String.valueOf(row.get("tableName"));
            tables.computeIfAbsent(schema + "." + name, ignored -> new TableInfo(schema, name)).columns.add(String.valueOf(row.get("columnName")).toLowerCase(Locale.ROOT));
        }
        return tables.values().stream().filter(t -> required.isEmpty() || required.stream().anyMatch(r -> t.columns.contains(r.toLowerCase(Locale.ROOT)))).findFirst().orElse(null);
    }

    private String latestSession(String school) {
        List<Map<String, Object>> rows = safe("SELECT TOP (1) session FROM TB_Session WHERE school_id=? ORDER BY id DESC", school);
        return rows.isEmpty() ? "" : String.valueOf(rows.get(0).getOrDefault("session", ""));
    }

    private String previousSession(String school, String current) {
        List<Map<String, Object>> rows = safe("SELECT TOP (2) session FROM TB_Session WHERE school_id=? ORDER BY id DESC", school);
        for (Map<String, Object> row : rows) {
            String value = String.valueOf(row.getOrDefault("session", ""));
            if (!value.equals(current)) return value;
        }
        return "";
    }

    private Map<String, Object> one(String sql, Object... args) {
        try { return jdbc.queryForMap(sql, args); } catch (DataAccessException ex) { return new LinkedHashMap<>(); }
    }

    private List<Map<String, Object>> safe(String sql, Object... args) {
        try { return jdbc.queryForList(sql, args); } catch (DataAccessException ex) { return List.of(); }
    }

    private static String normalize(String value) { return value == null ? "academic" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ROOT); }
    private static int number(Object value) { return value instanceof Number n ? n.intValue() : 0; }
    private static String qi(String identifier) { return "[" + identifier.replace("]", "]]" ) + "]"; }
    private static String whereFor(TableInfo table) { return table.schoolColumn() == null ? "" : " WHERE " + qi(table.schoolColumn()) + "=?"; }
    private static Object[] argsFor(TableInfo table, String school) { return table.schoolColumn() == null ? new Object[]{} : new Object[]{school}; }
    private static String andOrWhere(String where) { return where.isBlank() ? " WHERE " : " AND "; }
    private static Object[] append(Object[] values, Object value) { Object[] result = Arrays.copyOf(values, values.length + 1); result[values.length] = value; return result; }

    private static final class TableInfo {
        final String schema;
        final String table;
        final Set<String> columns = new HashSet<>();
        TableInfo(String schema, String table) { this.schema = schema; this.table = table; }
        String name() { return qi(schema) + "." + qi(table); }
        String schoolColumn() { return column("school_id", "schoolid", "school"); }
        String column(String... candidates) {
            for (String candidate : candidates) if (candidate != null && columns.contains(candidate.toLowerCase(Locale.ROOT))) return candidate.toLowerCase(Locale.ROOT);
            return null;
        }
    }
}
