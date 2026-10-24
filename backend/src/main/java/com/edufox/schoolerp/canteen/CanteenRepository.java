package com.edufox.schoolerp.canteen;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Direct table access for the migrated Canteen (legacy Tuckshop) module.
 * The old ASPX pages used procedures; these queries intentionally keep the
 * same tables, filters and result columns without depending on procedures.
 */
@Repository
@RequiredArgsConstructor
public class CanteenRepository {
    private final JdbcTemplate jdbc;

    private static String s(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static String upper(Object value) { return s(value).toUpperCase(Locale.ROOT); }
    private static Map<String, Object> row(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i + 1 < values.length; i += 2) result.put(String.valueOf(values[i]), values[i + 1]);
        return result;
    }
    private static String nextSession(String session) {
        String value = s(session);
        if (value.length() >= 11 && value.charAt(5) == '-') {
            String end = value.substring(7, 11).trim();
            try { return end + " - " + (Integer.parseInt(end) + 1); } catch (NumberFormatException ignored) { }
        }
        return value;
    }
    private static String currentYear(String session) {
        String value = s(session);
        return value.length() >= 4 ? value.substring(0, 4) : value;
    }
    private static String dateValue(Object value) {
        String date = s(value);
        if (date.isBlank()) return LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        return date;
    }
    private static BigDecimal decimal(Object value) {
        String v = s(value).replace(",", "");
        return v.isBlank() ? BigDecimal.ZERO : new BigDecimal(v);
    }

    public List<Map<String, Object>> sessions(String school) {
        return jdbc.queryForList("SELECT session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN LOWER(status)='default' THEN 0 ELSE 1 END,session DESC", school);
    }

    public String defaultSession(String school) {
        List<Map<String, Object>> rows = sessions(school);
        return rows.isEmpty() ? "" : s(rows.get(0).get("value"));
    }

    public Map<String, Object> bootstrap(String school, String requestedSession, String staff) {
        String session = s(requestedSession).isBlank() ? defaultSession(school) : requestedSession;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("session", session);
        result.put("sessions", sessions(school));
        result.put("classes", jdbc.queryForList("SELECT c.sec_id AS id,c.sec_id AS secId,c.class_id AS classId,cn.class_name AS className,c.section,cn.class_name+' ('+c.section+')' AS label FROM TB_Class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? ORDER BY c.class_id,c.sec_id", school));
        result.put("expenseGroups", expenseGroups(school));
        result.put("expenseNames", expenseNames(school));
        result.put("productGroups", productGroups(school));
        result.put("schoolId", school);
        result.put("staffId", staff);
        return result;
    }

    public List<Map<String, Object>> expenseGroups(String school) {
        return jdbc.queryForList("SELECT id,group_id AS groupId,group_id,group_name,status FROM tuckshop_expense_group WHERE school_id=? AND status=1 ORDER BY group_name", school);
    }

    public Map<String, Object> saveExpenseGroup(String id, Map<String, Object> body, String school, String staff, boolean update) {
        String name = s(body.get("groupName"));
        if (name.isBlank()) throw new IllegalArgumentException("Group name is required");
        if (update) {
            jdbc.update("UPDATE tuckshop_expense_group SET group_name=? WHERE school_id=? AND id=?", name, school, id);
            return row("message", "Record updated successfully");
        }
        Integer duplicate = jdbc.queryForObject("SELECT COUNT(1) FROM tuckshop_expense_group WHERE school_id=? AND group_name=? AND status=1", Integer.class, school, name);
        if (duplicate != null && duplicate > 0) throw new IllegalArgumentException("Record already exists");
        Integer groupId = jdbc.queryForObject("SELECT ISNULL(MAX(group_id),0)+1 FROM tuckshop_expense_group WHERE school_id=?", Integer.class, school);
        jdbc.update("INSERT INTO tuckshop_expense_group(group_id,group_name,school_id,created_date,created_by,modified_date,modified_by,status) VALUES(?,?,?,GETDATE(),?,NULL,NULL,1)", groupId, name, school, staff);
        return row("message", "Record saved successfully");
    }

    public void deleteExpenseGroup(String id, String school) {
        jdbc.update("UPDATE tuckshop_expense_group SET status=0 WHERE school_id=? AND id=?", school, id);
    }

    public List<Map<String, Object>> expenseNames(String school) {
        return jdbc.queryForList("SELECT n.id,n.expense_code AS expenseCode,n.expense_code,n.expense_name AS expenseName,n.expense_name,n.group_id AS groupId,n.status,g.group_name AS groupName FROM tuckshop_expense_name n LEFT JOIN tuckshop_expense_group g ON g.school_id=n.school_id AND g.group_id=n.group_id WHERE n.school_id=? AND n.status=1 ORDER BY n.expense_name", school);
    }

    public Map<String, Object> saveExpenseName(String id, Map<String, Object> body, String school, String staff, boolean update) {
        String name = s(body.get("expenseName"));
        String groupId = s(body.get("groupId"));
        if (name.isBlank() || groupId.isBlank()) throw new IllegalArgumentException("Expense name and group are required");
        if (update) {
            jdbc.update("UPDATE tuckshop_expense_name SET expense_name=? WHERE school_id=? AND id=?", name, school, id);
            return row("message", "Record updated successfully");
        }
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase(Locale.ROOT);
        jdbc.update("INSERT INTO tuckshop_expense_name(expense_code,group_id,expense_name,school_id,created_date,created_by,modified_date,modified_by,status) VALUES(?,?,?, ?,GETDATE(),?,NULL,NULL,1)", code, Integer.valueOf(groupId), name, school, staff);
        return row("message", "Record saved successfully");
    }

    public void deleteExpenseName(String id, String school) {
        jdbc.update("UPDATE tuckshop_expense_name SET status=0 WHERE school_id=? AND id=?", school, id);
    }

    public List<Map<String, Object>> productGroups(String school) {
        return jdbc.queryForList("SELECT rowid AS id,rowid,Product_code AS productCode,Product_code,Product_name AS productName,Product_name,school_id FROM tuckshop_Product_master WHERE school_id=? ORDER BY Product_name", school);
    }

    public Map<String, Object> saveProductGroup(String id, Map<String, Object> body, String school, String staff, boolean update) {
        String name = s(body.get("productName"));
        if (name.isBlank()) throw new IllegalArgumentException("Product group name is required");
        if (update) jdbc.update("UPDATE tuckshop_Product_master SET Product_name=?,modified_date=GETDATE() WHERE school_id=? AND rowid=?", name, school, id);
        else jdbc.update("INSERT INTO tuckshop_Product_master(Product_name,school_id,created_date) VALUES(?,?,GETDATE())", name, school);
        return row("message", update ? "Record updated successfully" : "Record saved successfully");
    }

    public void deleteProductGroup(String id, String school) {
        jdbc.update("DELETE FROM tuckshop_Product_master WHERE school_id=? AND rowid=?", school, id);
    }

    private String productDetailSql() {
        return """
            WITH product_detail AS (
              SELECT quantity,product_id,school_id,session FROM tuckshop_product_detail
              WHERE school_id=? AND LEFT(session,4)<=LEFT(?,4)
            ), account AS (
              SELECT ISNULL(SUM(ISNULL(TRY_CONVERT(int,qty),0)),0) qty,product_id,school_id,session
              FROM tuckshop_account WHERE school_id=? AND LEFT(session,4)<=LEFT(?,4)
              GROUP BY product_id,school_id,session
            ), data AS (
              SELECT p.Product_code AS productCode,p.p_id AS groupId,
                (SELECT TOP 1 Product_name FROM tuckshop_Product_master m WHERE m.school_id=p.school_id AND m.Product_code=p.Product_code) AS productGroup,
                p.product_id AS productId,p.Product_name AS productName,
                (SELECT ISNULL(SUM(ISNULL(TRY_CONVERT(int,quantity),0)),0)-ISNULL((SELECT SUM(qty) FROM account a WHERE a.school_id=? AND a.product_id=p.product_id AND a.session=?),0) FROM product_detail d WHERE d.school_id=? AND d.product_id=p.product_id AND d.session=?) AS remQty1,
                ISNULL((SELECT SUM(ISNULL(TRY_CONVERT(int,quantity),0)) FROM product_detail d WHERE d.school_id=? AND d.product_id=p.product_id AND d.session=?),0) AS addQty,
                p.Sell_Price AS sellPrice,p.MRP AS costPrice,
                ((SELECT ISNULL(SUM(ISNULL(TRY_CONVERT(int,quantity),0)),0)-ISNULL((SELECT SUM(qty) FROM account a WHERE a.school_id=? AND a.product_id=p.product_id AND a.session<>?),0) FROM product_detail d WHERE d.school_id=? AND d.product_id=p.product_id AND d.session<>?) + (SELECT ISNULL(SUM(ISNULL(TRY_CONVERT(int,quantity),0)),0) FROM product_detail d WHERE d.school_id=? AND d.product_id=p.product_id AND d.session=?)) AS totalQty,
                (SELECT ISNULL(SUM(ISNULL(TRY_CONVERT(int,qty),0)),0) FROM account a WHERE a.school_id=? AND a.product_id=p.product_id AND a.session=?) AS issueQty
              FROM tuckshop_Product p WHERE p.school_id=? AND p.status=1
            )
            SELECT *,CAST(ISNULL(totalQty,0) AS int)-CAST(ISNULL(issueQty,0) AS int) AS remQty FROM data ORDER BY productName
            """;
    }

    private Object[] productDetailParams(String school, String session) {
        return new Object[]{school, session, school, session, school, session, school, session, school, session, school, session, school, session, school, session, school, session, school};
    }

    public List<Map<String, Object>> products(String school, String session) {
        return jdbc.queryForList(productDetailSql(), productDetailParams(school, session));
    }

    public Map<String, Object> saveProduct(String productId, Map<String, Object> body, String school, String session, String staff, boolean update) {
        String groupCode = s(body.get("productCode"));
        String name = s(body.get("productName"));
        String sell = s(body.get("sellPrice"));
        String cost = s(body.get("costPrice"));
        if (groupCode.isBlank() || name.isBlank() || sell.isBlank() || cost.isBlank()) throw new IllegalArgumentException("Product group, name, cost price and sale price are required");
        if (!update) {
            Integer duplicate = jdbc.queryForObject("SELECT COUNT(1) FROM tuckshop_Product WHERE school_id=? AND Product_name=? AND status=1", Integer.class, school, name);
            if (duplicate != null && duplicate > 0) throw new IllegalArgumentException("Record already exists");
            Integer max = jdbc.queryForObject("SELECT MAX(TRY_CONVERT(int,REPLACE(product_id,'PID-',''))) FROM tuckshop_Product WHERE school_id=?", Integer.class, school);
            String newId = "PID-" + (max == null ? 10001 : max + 1);
            jdbc.update("INSERT INTO tuckshop_Product(Product_Code,Product_name,product_id,Sell_Price,MRP,status,created_by,school_id,created_date) VALUES(?,?,?,?,?,1,?,?,GETDATE())", groupCode, name, newId, decimal(sell), decimal(cost), staff, school);
            jdbc.update("INSERT INTO tuckshop_product_detail(product_id,quantity,session,school_id,created_by,created_date) VALUES(?,?,?,?,?,GETDATE())", newId, s(body.get("quantity")), session, school, staff);
            return row("message", "Record saved successfully", "productId", newId);
        }
        String add = s(body.get("addQuantity"));
        if (!add.isBlank()) jdbc.update("INSERT INTO tuckshop_product_detail(product_id,quantity,session,school_id,created_by,created_date) VALUES(?,?,?,?,?,GETDATE())", productId, add, session, school, staff);
        jdbc.update("UPDATE tuckshop_Product SET status=0 WHERE school_id=? AND product_id=?", school, productId);
        jdbc.update("INSERT INTO tuckshop_Product(Product_Code,Product_name,product_id,Sell_Price,MRP,status,created_by,school_id,created_date) VALUES(?,?,?,?,?,1,?,?,GETDATE())", groupCode, name, productId, decimal(sell), decimal(cost), staff, school);
        return row("message", "Record updated successfully", "productId", productId);
    }

    public void deleteProduct(String productId, String school) {
        jdbc.update("UPDATE tuckshop_Product SET status=0 WHERE school_id=? AND product_id=?", school, productId);
    }

    public List<Map<String, Object>> creditBalances(String school, String session) {
        return jdbc.queryForList("SELECT tc.id,ISNULL(tc.credit_balance,0) AS creditBalance,sm.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,cn.class_name AS className FROM TB_StudentMaster sm JOIN student_other_info so ON sm.school_id=so.school_id AND sm.registration_no=so.registration_no LEFT JOIN tuckshop_credit_balance tc ON tc.school_id=so.school_id AND tc.registration_no=so.registration_no AND sm.session=tc.session LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id WHERE so.school_id=? AND sm.session=? ORDER BY sm.registration_no", school, session);
    }

    public void updateCreditBalance(String registrationNo, String session, Object credit, String school, String staff) {
        Integer count = jdbc.queryForObject("SELECT COUNT(1) FROM tuckshop_credit_balance WHERE school_id=? AND registration_no=? AND session=?", Integer.class, school, registrationNo, session);
        if (count != null && count > 0) jdbc.update("UPDATE tuckshop_credit_balance SET credit_balance=?,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND registration_no=? AND session=?", decimal(credit), staff, school, registrationNo, session);
        else jdbc.update("INSERT INTO tuckshop_credit_balance(registration_no,school_id,credit_balance,session,created_by,created_date) VALUES(?,?,?,?,?,GETDATE())", registrationNo, school, decimal(credit), session, staff);
    }

    public List<Map<String, Object>> students(String school, String session, String secId) {
        String sql = "SELECT st.stu_id AS studentId,st.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')+'--'+COALESCE(so.father_name,'')))) AS nameWithFather,so.father_name AS fatherName,st.sec_id AS secId,st.class_id AS classId,cn.class_name AS className,cl.section FROM TB_StudentMaster st JOIN student_other_info so ON st.registration_no=so.registration_no AND st.school_id=so.school_id AND (st.uid=so.uid OR st.uid IS NULL OR so.uid IS NULL) JOIN TB_Class cl ON cl.sec_id=st.sec_id AND cl.school_id=st.school_id JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id WHERE st.school_id=? AND st.session=?";
        List<Object> params = new ArrayList<>(List.of(school, session));
        if (!s(secId).isBlank() && !"0".equals(secId)) { sql += " AND st.sec_id=?"; params.add(secId); }
        return jdbc.queryForList(sql + " ORDER BY name", params.toArray());
    }

    public List<Map<String, Object>> studentByRegistration(String school, String session, String registrationNo) {
        return jdbc.queryForList("SELECT TOP 1 st.stu_id AS studentId,st.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,so.father_name AS fatherName,st.sec_id AS secId,st.class_id AS classId,cn.class_name AS className,(SELECT TOP 1 promoted.class_name FROM class_name promoted WHERE promoted.school_id=st.school_id AND promoted.id=cn.promoted_class_id) AS promotedClassName,cl.section,ISNULL(TRY_CONVERT(decimal(18,2),NULLIF(tc.credit_balance,'')),0) AS creditBalance,(ISNULL((SELECT SUM(charges) FROM tuckshop_assign_expense ae WHERE ae.school_id=st.school_id AND ae.registration_no=st.registration_no AND ae.session=?),0)+ISNULL((SELECT SUM(TRY_CONVERT(decimal(18,2),total_price)) FROM tuckshop_account ta WHERE ta.school_id=st.school_id AND ta.registration_no=st.registration_no AND ta.session=?),0)) AS debitBalance FROM TB_StudentMaster st JOIN student_other_info so ON st.registration_no=so.registration_no AND st.school_id=so.school_id LEFT JOIN TB_Class cl ON cl.sec_id=st.sec_id AND cl.school_id=st.school_id LEFT JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id LEFT JOIN tuckshop_credit_balance tc ON tc.school_id=st.school_id AND tc.registration_no=st.registration_no AND tc.session=? WHERE st.school_id=? AND st.session=? AND st.registration_no=?", session, session, session, school, session, registrationNo);
    }

    public Map<String, Object> studentBalance(String school, String session, String registrationNo) {
        List<Map<String, Object>> rows = studentByRegistration(school, session, registrationNo);
        if (rows.isEmpty()) throw new IllegalArgumentException("No Record Found");
        return rows.get(0);
    }

    public List<Map<String, Object>> registrationSuggestions(String school, String session, String prefix) {
        return jdbc.queryForList("SELECT TOP 10 registration_no AS registrationNo FROM TB_StudentMaster WHERE school_id=? AND session=? AND registration_no LIKE ? ORDER BY registration_no", school, session, s(prefix) + "%");
    }

    public List<Map<String, Object>> expenseHistory(String school, String session, String registrationNo) {
        return jdbc.queryForList("SELECT ae.id,ae.expense_code AS expenseCode,ae.registration_no AS registrationNo,en.expense_name AS expenseName,ae.trans_date AS transDate,ae.charges,ae.description,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,cn.class_name AS className FROM tuckshop_assign_expense ae LEFT JOIN tuckshop_expense_name en ON ae.school_id=en.school_id AND ae.expense_code=en.expense_code LEFT JOIN TB_StudentMaster sm ON ae.school_id=sm.school_id AND ae.registration_no=sm.registration_no AND ae.session=sm.session LEFT JOIN student_other_info so ON sm.school_id=so.school_id AND sm.registration_no=so.registration_no LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id WHERE ae.school_id=? AND ae.registration_no=? AND ae.session=? ORDER BY TRY_CONVERT(date,ae.trans_date)", school, registrationNo, session);
    }

    public List<Map<String, Object>> expenseSummary(String school, String session, String registrationNo) {
        return jdbc.queryForList("SELECT expenseName,charges FROM (SELECT en.expense_name AS expenseName,SUM(ae.charges) AS charges FROM tuckshop_assign_expense ae LEFT JOIN tuckshop_expense_name en ON ae.school_id=en.school_id AND ae.expense_code=en.expense_code WHERE ae.school_id=? AND ae.registration_no=? AND ae.session=? GROUP BY en.expense_name UNION ALL SELECT 'TUCKSHOP' AS expenseName,SUM(TRY_CONVERT(decimal(18,2),ta.total_price)) FROM tuckshop_account ta WHERE ta.school_id=? AND ta.registration_no=? AND ta.session=?) x WHERE charges IS NOT NULL UNION ALL SELECT 'TOTAL',SUM(charges) FROM (SELECT SUM(ae.charges) charges FROM tuckshop_assign_expense ae WHERE ae.school_id=? AND ae.registration_no=? AND ae.session=? UNION ALL SELECT SUM(TRY_CONVERT(decimal(18,2),ta.total_price)) FROM tuckshop_account ta WHERE ta.school_id=? AND ta.registration_no=? AND ta.session=?) t WHERE charges IS NOT NULL", school, registrationNo, session, school, registrationNo, session, school, registrationNo, session, school, registrationNo, session);
    }

    public void saveAssignments(String school, String session, String registrationNo, String transDate, List<Map<String, Object>> rows, String staff) {
        if (rows == null || rows.isEmpty()) throw new IllegalArgumentException("Add at least one expense");
        String year = currentYear(session);
        int month = LocalDate.now().getMonthValue();
        String date = dateValue(transDate);
        String prefix = year + month;
        Integer next = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int,REPLACE(trans_id,?,''))),0)+1 FROM tuckshop_assign_expense WHERE school_id=? AND trans_id LIKE ?", Integer.class, prefix, school, "%" + prefix + "%");
        String transId = prefix + (next == null ? 1 : next);
        int inserted = 0;
        for (Map<String, Object> item : rows) {
            String code = s(item.get("expenseCode"));
            if (code.isBlank()) continue;
            jdbc.update("INSERT INTO tuckshop_assign_expense(registration_no,session,expense_code,charges,description,trans_id,trans_date,school_id,created_by,created_date) VALUES(?,?,?,?,?,?,?,?,?,GETDATE())", registrationNo, session, code, decimal(item.get("amount")), s(item.get("description")), transId, date, school, staff);
            inserted++;
        }
        if (inserted == 0) throw new IllegalArgumentException("Select an expense and enter amount");
    }

    public void updateAssignment(String id, String school, String session, String registrationNo, Map<String, Object> body) {
        jdbc.update("UPDATE tuckshop_assign_expense SET expense_code=?,trans_date=?,charges=? WHERE id=? AND school_id=? AND registration_no=? AND session=?", s(body.get("expenseCode")), dateValue(body.get("transDate")), decimal(body.get("charges")), id, school, registrationNo, session);
    }

    public void deleteAssignment(String id, String school, String session, String registrationNo) {
        jdbc.update("DELETE FROM tuckshop_assign_expense WHERE id=? AND school_id=? AND registration_no=? AND session=?", id, school, registrationNo, session);
    }

    public List<Map<String, Object>> debitCreditReport(String school, String session, String classId, String secId) {
        String sql = "SELECT st.registration_no AS idNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,cn.class_name+' ['+cl.section+']' AS className,ISNULL(TRY_CONVERT(decimal(18,2),NULLIF(tc.credit_balance,'')),0) AS creditBalance,exp.debitBalance,CASE WHEN exp.debitBalance>ISNULL(TRY_CONVERT(decimal(18,2),NULLIF(tc.credit_balance,'')),0) THEN '+'+CONVERT(varchar(100),exp.debitBalance-ISNULL(TRY_CONVERT(decimal(18,2),NULLIF(tc.credit_balance,'')),0)) ELSE CONVERT(varchar(100),exp.debitBalance-ISNULL(TRY_CONVERT(decimal(18,2),NULLIF(tc.credit_balance,'')),0)) END AS balance FROM TB_StudentMaster st JOIN student_other_info so ON st.school_id=so.school_id AND st.registration_no=so.registration_no LEFT JOIN TB_Class cl ON cl.sec_id=st.sec_id AND cl.school_id=st.school_id LEFT JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id LEFT JOIN tuckshop_credit_balance tc ON tc.school_id=st.school_id AND tc.registration_no=st.registration_no AND tc.session=st.session LEFT JOIN (SELECT registration_no,SUM(debit) AS debitBalance FROM (SELECT registration_no,SUM(charges) debit FROM tuckshop_assign_expense WHERE school_id=? AND session=? GROUP BY registration_no UNION ALL SELECT registration_no,SUM(TRY_CONVERT(decimal(18,2),total_price)) FROM tuckshop_account WHERE school_id=? AND session=? GROUP BY registration_no) d GROUP BY registration_no) exp ON exp.registration_no=st.registration_no WHERE st.school_id=? AND st.session=?";
        List<Object> params = new ArrayList<>(List.of(school, session, school, session, school, session));
        if (!s(classId).isBlank() && !"0".equals(classId)) { sql += " AND st.class_id=?"; params.add(classId); }
        if (!s(secId).isBlank() && !"0".equals(secId)) { sql += " AND st.sec_id=?"; params.add(secId); }
        return jdbc.queryForList(sql + " ORDER BY st.registration_no", params.toArray());
    }

    public Map<String, Object> invoice(String school, String session, String registrationNo, String classId, String secId) {
        List<Map<String, Object>> students = studentByRegistration(school, session, registrationNo);
        if (students.isEmpty()) throw new IllegalArgumentException("Student is required");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("student", students.get(0));
        String next = nextSession(session);
        String effectiveClass = s(students.get(0).get("classId"));
        result.put("feeCharges", jdbc.queryForList("SELECT CAST(1 AS int) AS sno,CAST(fhc.fee_charges AS decimal(18,2)) AS feeCharge,fh.fee_head AS feeHead FROM fee_head_charge fhc LEFT JOIN fee_head_master fh ON fh.school_id=fhc.school_id AND fh.session=fhc.session AND fh.fee_head_id=fhc.fee_head_id AND fh.status=1 WHERE fhc.school_id=? AND fhc.session=? AND fhc.class_id=(SELECT promoted_class_id FROM class_name WHERE school_id=? AND id=?) UNION ALL SELECT CAST(2 AS int),SUM(CAST(fhc.fee_charges AS decimal(18,2))),'TOTAL' FROM fee_head_charge fhc WHERE fhc.school_id=? AND fhc.session=? AND fhc.class_id=(SELECT promoted_class_id FROM class_name WHERE school_id=? AND id=?)", school, next, school, effectiveClass, school, next, school, effectiveClass));
        result.put("credit", jdbc.queryForList("SELECT ISNULL(TRY_CONVERT(decimal(18,2),NULLIF(credit_balance,'')),0) AS creditBalance,session FROM tuckshop_credit_balance WHERE school_id=? AND session=? AND registration_no=?", school, session, registrationNo));
        result.put("expenses", jdbc.queryForList("SELECT CAST(1 AS int) AS sno,expenseName,charges FROM (SELECT en.expense_name AS expenseName,SUM(ae.charges) charges FROM tuckshop_assign_expense ae LEFT JOIN tuckshop_expense_name en ON en.school_id=ae.school_id AND en.expense_code=ae.expense_code WHERE ae.school_id=? AND ae.session=? AND ae.registration_no=? GROUP BY en.expense_name UNION ALL SELECT 'TUCKSHOP & STATIONARY',SUM(TRY_CONVERT(decimal(18,2),total_price)) FROM tuckshop_account WHERE school_id=? AND session=? AND registration_no=?) x WHERE charges IS NOT NULL UNION ALL SELECT CAST(100 AS int),'TOTAL',SUM(charges) FROM (SELECT SUM(ae.charges) charges FROM tuckshop_assign_expense ae WHERE ae.school_id=? AND ae.session=? AND ae.registration_no=? UNION ALL SELECT SUM(TRY_CONVERT(decimal(18,2),total_price)) FROM tuckshop_account WHERE school_id=? AND session=? AND registration_no=?) y", school, session, registrationNo, school, session, registrationNo, school, session, registrationNo, school, session, registrationNo));
        BigDecimal feeTotal = feeTotal(result);
        BigDecimal expenseTotal = expenseTotal(result);
        BigDecimal creditTotal = creditTotal(result);
        BigDecimal netAmount = feeTotal != null && expenseTotal != null && creditTotal != null
            ? feeTotal.add(expenseTotal).subtract(creditTotal) : null;
        result.put("feeTotal", feeTotal);
        result.put("expenseTotal", expenseTotal);
        result.put("creditTotal", creditTotal);
        result.put("netAmount", netAmount);
        BigDecimal balance = expenseTotal != null && creditTotal != null
            ? expenseTotal.subtract(creditTotal) : null;
        String balanceLabel = creditTotal != null && expenseTotal != null && creditTotal.compareTo(expenseTotal) > 0
            ? "Credit Balance" : "Debit Balance";
        String balanceValue = balance == null ? null
            : (balance.signum() < 0 ? "- " + balance.abs().toPlainString() : "+ " + balance.toPlainString());
        result.put("summary", List.of(
            row("sno", 1, "total", "Total", "totalFee", feeTotal == null ? null : feeTotal.toPlainString(), "total_fee", feeTotal == null ? null : feeTotal.toPlainString()),
            row("sno", 2, "total", balanceLabel, "totalFee", balanceValue, "total_fee", balanceValue),
            row("sno", 3, "total", "Net Amount Payable To School", "totalFee", netAmount == null ? null : netAmount.toPlainString(), "total_fee", netAmount == null ? null : netAmount.toPlainString())
        ));
        result.put("installment", jdbc.queryForList("SELECT amount AS inst2,(CAST(? AS decimal(18,2))-amount) AS inst1,? AS toSession FROM tuckshop_classwise_fee WHERE school_id=? AND session=? AND class_id=(SELECT promoted_class_id FROM class_name WHERE id=? AND school_id=?) AND installment_no=2", netAmount, next, school, next, effectiveClass, school));
        result.put("session", session);
        result.put("fromSession", session.length() >= 11 ? session.substring(7, 11) : session);
        result.put("toSession", next.length() >= 11 ? next.substring(7, 11) : next);
        result.put("school", jdbc.queryForList("SELECT TOP 1 schoolname,address,web_address,logo_path AS logo,bank_name AS bankName,bank_acc AS bankAccount,bank_branch AS bankBranch,IFSC AS ifsc,bank_name_new AS bankNameNew,bank_acc_new AS bankAccountNew,IFSC_new AS ifscNew,mobile,mobile1,CASE WHEN NULLIF(principal_signature,'') IS NULL THEN '' ELSE COALESCE(web_address,'')+'/'+principal_signature END AS principalSignature FROM schoolmaster WHERE school_id=?", school));
        return result;
    }

    private BigDecimal feeTotal(Map<String, Object> invoice) {
        Object raw = invoice.get("feeCharges");
        BigDecimal total = null;
        if (raw instanceof List<?> list) for (Object value : list) if (value instanceof Map<?, ?> m && !"TOTAL".equalsIgnoreCase(s(m.get("feeHead")))) {
            String valueText = s(m.get("feeCharge"));
            if (!valueText.isBlank()) total = total == null ? decimal(valueText) : total.add(decimal(valueText));
        }
        return total;
    }

    private BigDecimal expenseTotal(Map<String, Object> invoice) {
        Object raw = invoice.get("expenses");
        BigDecimal total = null;
        if (raw instanceof List<?> list) for (Object value : list) if (value instanceof Map<?, ?> m && !"TOTAL".equalsIgnoreCase(s(m.get("expenseName")))) {
            String valueText = s(m.get("charges"));
            if (!valueText.isBlank()) total = total == null ? decimal(valueText) : total.add(decimal(valueText));
        }
        return total;
    }

    private BigDecimal creditTotal(Map<String, Object> invoice) {
        Object raw = invoice.get("credit");
        if (raw instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map<?, ?> m) return decimal(m.get("creditBalance"));
        return null;
    }

    private String productBalanceSql() {
        return """
            WITH product_detail AS (SELECT quantity,product_id,school_id,session FROM tuckshop_product_detail WHERE school_id=? AND LEFT(session,4)<=LEFT(?,4)), account AS (SELECT ISNULL(SUM(ISNULL(TRY_CONVERT(int,qty),0)),0) qty,product_id,school_id,session FROM tuckshop_account WHERE school_id=? AND LEFT(session,4)<=LEFT(?,4) GROUP BY product_id,school_id,session), data AS (
            SELECT p.product_id AS productId,p.product_name AS productName,
            ((SELECT ISNULL(SUM(ISNULL(TRY_CONVERT(int,quantity),0)),0)-ISNULL((SELECT SUM(qty) FROM account a WHERE a.school_id=? AND a.product_id=p.product_id AND a.session<>?),0) FROM product_detail d WHERE d.school_id=? AND d.product_id=p.product_id AND d.session<>?) +(SELECT ISNULL(SUM(ISNULL(TRY_CONVERT(int,quantity),0)),0) FROM product_detail d WHERE d.school_id=? AND d.product_id=p.product_id AND d.session=?)) AS totalQty,
            ISNULL((SELECT SUM(ISNULL(TRY_CONVERT(int,qty),0)) FROM account a WHERE a.school_id=? AND a.product_id=p.product_id AND a.session=?),0) AS issueQty,
            ISNULL((SELECT TOP 1 TRY_CONVERT(decimal(18,2),price) FROM tuckshop_account a WHERE a.school_id=p.school_id AND a.product_id=p.product_id AND a.status='active' AND a.session=? AND a.acc_type='S'),0) AS sp,
            ISNULL((SELECT TOP 1 TRY_CONVERT(decimal(18,2),cost_price) FROM tuckshop_account a WHERE a.school_id=p.school_id AND a.product_id=p.product_id AND a.status='active' AND a.session=? AND a.acc_type='S'),0) AS costPrice
            FROM tuckshop_Product p WHERE p.school_id=? AND p.status=1)
            SELECT productName,totalQty,issueQty,CAST(totalQty AS decimal(18,2))*costPrice AS totalCp,sp,costPrice,CAST(issueQty AS decimal(18,2))*sp AS totalSp,CAST(totalQty AS int)-CAST(issueQty AS int) AS remQty,(CAST(totalQty AS int)-CAST(issueQty AS int))*costPrice AS totalAmt FROM data ORDER BY productName
            """;
    }

    public List<Map<String, Object>> productBalance(String school, String session) {
        return jdbc.queryForList(productBalanceSql(), school, session, school, session, school, session, school, session, school, session, school, session, session, session, school);
    }

    public List<Map<String, Object>> assignExpenseReport(String school, String session) {
        return jdbc.queryForList("SELECT 1 AS sno,s.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,ISNULL((SELECT SUM(CAST(charges AS decimal(18,2))) FROM tuckshop_assign_expense ae WHERE ae.school_id=s.school_id AND ae.session=s.session AND ae.registration_no=s.registration_no),0) AS charges FROM TB_StudentMaster s LEFT JOIN student_other_info so ON s.school_id=so.school_id AND s.registration_no=so.registration_no WHERE s.school_id=? AND s.session=? UNION ALL SELECT 2,'','Total',SUM(ISNULL(CAST(charges AS decimal(18,2)),0)) FROM tuckshop_assign_expense WHERE school_id=? AND session=? ORDER BY sno,registrationNo", school, session, school, session);
    }

    public List<Map<String, Object>> productIssues(String school, String session, String userType, String registrationNo) {
        String sql = "SELECT registration_no AS registrationNo,req_no AS reqNo,CONVERT(varchar(100),TRY_CONVERT(datetime,trans_date),106) AS transDate,product_name AS productName,qty AS quantity,price,total_price AS totalPrice,CASE WHEN acc_type='S' THEN 'Student' WHEN acc_type='F' THEN 'Office' ELSE 'Staff' END AS userType FROM tuckshop_account WHERE school_id=? AND session=? AND status='active'";
        List<Object> params = new ArrayList<>(List.of(school, session));
        if (!s(userType).isBlank() && !"0".equals(userType) && !"-1".equals(userType)) { sql += " AND acc_type=?"; params.add(userType); }
        if (!s(registrationNo).isBlank()) { sql += " AND registration_no=? AND acc_type='S'"; params.add(registrationNo); }
        return jdbc.queryForList(sql + " ORDER BY userType,TRY_CONVERT(int,req_no)", params.toArray());
    }

    public List<Map<String, Object>> productReport(String school, String session, String userType) {
        if ("0".equals(userType) || s(userType).isBlank() || "-1".equals(userType)) return jdbc.queryForList("SELECT SUM(qty) AS quantity,ISNULL(SUM(total_price),0) AS total,CASE WHEN acc_type='S' THEN 'Student' WHEN acc_type='F' THEN 'Office' ELSE 'Staff' END AS userType FROM tuckshop_account WHERE school_id=? AND session=? AND status='active' GROUP BY acc_type UNION ALL SELECT SUM(qty),ISNULL(SUM(total_price),0),'TOTAL' FROM tuckshop_account WHERE school_id=? AND session=? AND status='active'", school, session, school, session);
        String type = "1".equals(userType) ? "S" : "2".equals(userType) ? "F" : "ST";
        String label = "1".equals(userType) ? "Student" : "2".equals(userType) ? "Office" : "Staff";
        return jdbc.queryForList("SELECT COUNT(qty) AS quantity,ISNULL(SUM(total_price),0) AS total,? AS userType FROM tuckshop_account WHERE school_id=? AND session=? AND status='active' AND LOWER(acc_type)=LOWER(?)", label, school, session, type);
    }
}
