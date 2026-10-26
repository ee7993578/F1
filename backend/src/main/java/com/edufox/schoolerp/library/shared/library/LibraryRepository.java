package com.edufox.schoolerp.library.shared.library;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

/**
 * Direct-SQL data access for the migrated Library module.
 *
 * The old ASPX pages call stored procedures.  The migration deliberately keeps
 * the same tables/columns but performs the equivalent reads and writes here so
 * the new application has no procedure dependency.
 */
@Repository
@RequiredArgsConstructor
public class LibraryRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> sessions(String school) {
        return jdbc.queryForList("SELECT session AS value,session AS label,status FROM tb_session WHERE school_id=? ORDER BY CASE WHEN LOWER(status)='default' THEN 0 ELSE 1 END,session DESC", school);
    }

    public String defaultSession(String school) {
        List<Map<String, Object>> rows = sessions(school);
        return rows.isEmpty() ? "" : text(rows.get(0).get("value"));
    }

    public Map<String, Object> bootstrap(String school, String session, String staff) {
        if (blank(session)) session = defaultSession(school);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("session", session);
        out.put("sessions", sessions(school));
        out.put("classes", jdbc.queryForList("SELECT c.sec_id AS id,c.sec_id AS secId,c.class_id AS classId,cn.class_name AS className,c.section,cn.class_name+' ('+c.section+')' AS label FROM tb_class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? ORDER BY c.class_id,c.section,c.sec_id", school));
        out.put("categories", masterRows("category", school, session));
        out.put("frequencies", masterRows("frequency", school, session));
        out.put("languages", masterRows("language", school, session));
        out.put("types", masterRows("item-type", school, session));
        out.put("publisherCategories", masterRows("publisher-category", school, session));
        out.put("supplierCategories", masterRows("supplier-category", school, session));
        out.put("publishers", publishers(school, session));
        out.put("suppliers", suppliers(school, session));
        out.put("members", jdbc.queryForList("SELECT id,Member AS name FROM member ORDER BY id"));
        out.put("months", jdbc.queryForList("SELECT month_id AS id,month_id AS value,month_id AS label,month_name AS name FROM tb_month ORDER BY id"));
        out.put("states", jdbc.queryForList("SELECT State_Id AS id,State_Name AS name FROM State ORDER BY State_Name"));
        out.put("districts", jdbc.queryForList("SELECT City_Id AS id,City_Name AS name,State_id AS stateId FROM Districts ORDER BY City_Name"));
        out.put("schoolId", school);
        out.put("staffId", staff);
        return out;
    }

    public List<Map<String, Object>> masterRows(String kind, String school, String session) {
        return switch (normalize(kind)) {
            case "language" -> jdbc.queryForList("SELECT itemlang_id AS id,itemlang_id AS itemlangId,code,name,remark,session FROM library_item_langmaster WHERE school_id=? ORDER BY name", school);
            case "frequency" -> jdbc.queryForList("SELECT itemf_id AS id,itemf_id AS itemfId,code,name,remark,session FROM library_item_frequency WHERE school_id=? ORDER BY name", school);
            case "category" -> jdbc.queryForList("SELECT itemcat_id AS id,itemcat_id AS itemcatId,code,name,unit,remark,session FROM library_item_category WHERE school_id=? ORDER BY name", school);
            case "publisher-category" -> jdbc.queryForList("SELECT pubcat_id AS id,pubcat_id AS pubcatId,code,name,remark,session FROM library_publisher_catmaster WHERE school_id=? ORDER BY name", school);
            case "supplier-category" -> jdbc.queryForList("SELECT supcat_id AS id,supcat_id AS supcatId,code,name,remark,session FROM library_sup_category WHERE school_id=? ORDER BY name", school);
            case "item-type" -> jdbc.queryForList("SELECT item_type_id AS id,item_type_id AS itemTypeId,item_type AS name,school_id AS schoolId FROM library_Item_type WHERE school_id=? ORDER BY item_type", school);
            default -> throw new IllegalArgumentException("Unsupported Library master: " + kind);
        };
    }

    public Map<String, Object> masterSave(String kind, String id, Map<String, Object> body, String school, String session, String staff, boolean update) {
        String k = normalize(kind), code = upper(text(body.get("code"))), name = upper(text(body.get("name"))), remark = upper(text(body.get("remark")));
        if (name.isBlank()) throw new IllegalArgumentException("Name is required");
        if ("item-type".equals(k)) {
            String token = update ? id : token();
            if (update) jdbc.update("UPDATE library_Item_type SET item_type=? WHERE item_type_id=? AND school_id=?", name, id, school);
            else jdbc.update("INSERT INTO library_Item_type(item_type,item_type_id,school_id) VALUES(?,?,?)", name, token, school);
            return row("id", token, "message", update ? "Item type updated successfully" : "Item type defined successfully");
        }
        Master m = master(k);
        String token = update ? id : token();
        if (update) {
            String sql = "UPDATE " + m.table + " SET code=?,name=?,remark=?,modified_by=?,modified_date=GETDATE()" + (m.unit ? ",unit=?" : "") + " WHERE " + m.idColumn + "=? AND school_id=?";
            if (m.unit) jdbc.update(sql, code, name, remark, staff, text(body.get("unit")), id, school);
            else jdbc.update(sql, code, name, remark, staff, id, school);
        } else {
            String sql = "INSERT INTO " + m.table + "(" + m.idColumn + ",code,name,remark,session,school_id,created_by,created_date" + (m.unit ? ",unit" : "") + ") VALUES(?,?,?, ?,?,?,?,GETDATE()" + (m.unit ? ",?" : "") + ")";
            if (m.unit) jdbc.update(sql, token, code, name, remark, session, school, staff, text(body.get("unit")));
            else jdbc.update(sql, token, code, name, remark, session, school, staff);
        }
        return row("id", token, "message", update ? "Record updated successfully" : "Record submitted successfully");
    }

    public void masterDelete(String kind, String id, String school) {
        String k = normalize(kind);
        if ("item-type".equals(k)) {
            Integer used = jdbc.queryForObject("SELECT COUNT(1) FROM library_item_entry WHERE school_id=? AND item_type=?", Integer.class, school, id);
            if (used != null && used > 0) throw new IllegalArgumentException("This item type is already used by an item");
            jdbc.update("DELETE FROM library_Item_type WHERE school_id=? AND item_type_id=?", school, id);
            return;
        }
        Master m = master(k);
        jdbc.update("DELETE FROM " + m.table + " WHERE school_id=? AND " + m.idColumn + "=?", school, id);
    }

    public List<Map<String, Object>> publishers(String school, String session) {
        return jdbc.queryForList("SELECT p.id,p.publisher_id AS publisherId,p.code,p.publisher_cat AS publisherCategory,p.name,p.address,p.state,p.city,p.zip,p.remark,p.phone1,p.phone2,p.mobile,p.fax,p.email,COALESCE(c.name,'') AS categoryName FROM library_publisher_entrymaster p LEFT JOIN library_publisher_catmaster c ON c.pubcat_id=p.publisher_cat AND c.school_id=p.school_id WHERE p.school_id=? ORDER BY p.name", school);
    }

    public List<Map<String, Object>> suppliers(String school, String session) {
        return jdbc.queryForList("SELECT s.id,s.supplier_id AS supplierId,s.code,s.sup_cat AS supplierCategory,s.name,s.address,s.state,s.city,s.zip,s.remark,s.phone1,s.phone2,s.mobile,s.fax,s.email,COALESCE(c.name,'') AS categoryName FROM library_supplier_entry s LEFT JOIN library_sup_category c ON c.supcat_id=s.sup_cat AND c.school_id=s.school_id WHERE s.school_id=? ORDER BY s.name", school);
    }

    public List<Map<String, Object>> rules(String school, String session) {
        return jdbc.queryForList("SELECT r.id,r.cat_id AS ruleId,r.cat_type AS memberType,r.item_category AS itemCategory,r.class_id AS classId,COALESCE(cn.class_name,CONVERT(varchar(50),r.class_id)) AS className,r.cautionmny_lib AS cautionMoneyLibrary,r.cautionmny_bb AS cautionMoneyBookBank,r.mncharge AS monthlyCharge,r.fine_lib AS fineLibrary,r.fine_bb AS fineBookBank,r.max_item_lib AS maxItemLibrary,r.max_item_bb AS maxItemBookBank,r.days_return_lib AS daysReturnLibrary,r.days_return_bb AS daysReturnBookBank,r.remark,r.session FROM library_items_rule r LEFT JOIN class_name cn ON cn.id=TRY_CONVERT(int,r.class_id) AND cn.school_id=r.school_id WHERE r.school_id=? AND (r.session=? OR ISNULL(r.session,'')='') ORDER BY r.id DESC", school, session);
    }

    public Map<String, Object> saveRule(Map<String, Object> body, String id, String school, String session, String staff, boolean memberCategory) {
        String ruleId = id.isBlank() ? token() : id;
        if (!id.isBlank()) jdbc.update("DELETE FROM library_items_rule WHERE school_id=? AND cat_id=?", school, id);
        List<String> classIds = strings(body.get("classIds"));
        if (classIds.isEmpty()) classIds.add(text(body.get("classId")));
        String classes = String.join(",", classIds.stream().filter(x -> !x.isBlank()).toList());
        List<String> itemCategories = strings(body.get("itemCategories"));
        if (itemCategories.isEmpty() && !text(body.get("itemCategory")).isBlank()) itemCategories.add(text(body.get("itemCategory")));
        String categories = memberCategory ? text(body.get("categoryName")) : String.join(",", itemCategories);
        jdbc.update("INSERT INTO library_items_rule(cat_id,cat_type,item_category,class_id,department_id,designation_id,cautionmny_lib,cautionmny_bb,mncharge,fine_lib,fine_bb,max_item_lib,max_item_bb,days_return_lib,days_return_bb,remark,school_id,session,created_by,created_date) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE())",
                ruleId, text(body.get("memberType")), categories, classes, text(body.get("departmentId")), text(body.get("designationId")), integer(body.get("cautionMoneyLibrary")), integer(body.get("cautionMoneyBookBank")), integer(body.get("monthlyCharge")), integer(body.get("fineLibrary")), integer(body.get("fineBookBank")), integer(body.get("maxItemLibrary")), integer(body.get("maxItemBookBank")), integer(body.get("daysReturnLibrary")), integer(body.get("daysReturnBookBank")), text(body.get("remark")), school, session, staff);
        return row("id", ruleId, "message", memberCategory ? "Member category saved successfully" : "Item rule saved successfully");
    }

    public void deleteRule(String id, String school) { jdbc.update("DELETE FROM library_items_rule WHERE school_id=? AND cat_id=?", school, id); }

    public List<Map<String, Object>> memberCategories(String school, String session) {
        return jdbc.queryForList("SELECT r.cat_id AS id,r.cat_id AS categoryId,r.item_category AS categoryName,r.cat_type AS categoryType,r.class_id AS classId,r.cautionmny_lib AS cautionMoneyLibrary,r.cautionmny_bb AS cautionMoneyBookBank,r.mncharge AS monthlyCharge,r.fine_lib AS fineLibrary,r.fine_bb AS fineBookBank,r.max_item_lib AS maxItemsLibrary,r.max_item_bb AS maxItemsBookBank,r.days_return_lib AS daysReturnLibrary,r.days_return_bb AS daysReturnBookBank,r.remark,r.session FROM library_items_rule r WHERE r.school_id=? AND (r.session=? OR ISNULL(r.session,'')='') ORDER BY r.id DESC", school, session);
    }

    public Map<String, Object> saveContact(String type, String id, Map<String, Object> b, String school, String session, String staff, boolean update) {
        boolean pub = "publisher".equals(normalize(type));
        String table = pub ? "library_publisher_entrymaster" : "library_supplier_entry";
        String idCol = pub ? "publisher_id" : "supplier_id";
        String catCol = pub ? "publisher_cat" : "sup_cat";
        String token = update ? id : token();
        String sql = (update ? "UPDATE " + table + " SET " : "INSERT INTO " + table + "(" + idCol + ",")
                + (update ? "code=?," : "code,")
                + (update ? "" : "") + "name,address,state,city,zip,remark,phone1,phone2,mobile,fax,email," + catCol + "=?";
        if (update) {
            sql = "UPDATE " + table + " SET code=?,name=?,address=?,state=?,city=?,zip=?,remark=?,phone1=?,phone2=?,mobile=?,fax=?,email=?," + catCol + "=?,modified_by=?,modified_date=GETDATE() WHERE " + idCol + "=? AND school_id=?";
            jdbc.update(sql, text(b.get("code")), text(b.get("name")), text(b.get("address")), text(b.get("state")), text(b.get("city")), numberOrNull(b.get("zip")), text(b.get("remark")), text(b.get("phone1")), text(b.get("phone2")), text(b.get("mobile")), text(b.get("fax")), text(b.get("email")), text(b.get("category")), staff, id, school);
        } else {
            sql = "INSERT INTO " + table + "(" + idCol + ",code,name,address,state,city,zip,remark,phone1,phone2,mobile,fax,email," + catCol + ",session,school_id,created_by,created_date) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE())";
            jdbc.update(sql, token, text(b.get("code")), text(b.get("name")), text(b.get("address")), text(b.get("state")), text(b.get("city")), numberOrNull(b.get("zip")), text(b.get("remark")), text(b.get("phone1")), text(b.get("phone2")), text(b.get("mobile")), text(b.get("fax")), text(b.get("email")), text(b.get("category")), session, school, staff);
        }
        return row("id", token, "message", update ? "Entry updated successfully" : "Entry saved successfully");
    }

    public void deleteContact(String type, String id, String school) {
        String table = "publisher".equals(normalize(type)) ? "library_publisher_entrymaster" : "library_supplier_entry";
        String col = "publisher".equals(normalize(type)) ? "publisher_id" : "supplier_id";
        jdbc.update("DELETE FROM " + table + " WHERE school_id=? AND " + col + "=?", school, id);
    }

    public List<Map<String, Object>> items(String school, String session, String search) {
        String sql = "SELECT e.id,e.item_acc_no AS accessionNo,e.lib_entry_date AS libraryEntryDate,e.item_category AS categoryId,COALESCE(c.name,e.item_category) AS categoryName,e.title,e.subtitle,e.no_of_items AS totalBooks,e.item_classification_no AS classificationNo,e.serial_no AS serialNo,e.vol_part AS volumePart,e.item_language AS languageId,COALESCE(l.name,e.item_language) AS languageName,e.item_type AS typeId,COALESCE(t.item_type,e.item_type) AS typeName,e.itemf_id AS frequencyId,COALESCE(fr.name,e.itemf_id) AS frequencyName,e.publication_date AS publicationDate,e.publisher_id AS publisherId,COALESCE(p.name,e.publisher_id) AS publisherName,e.author,e.keyword,e.isbn_no AS isbnNo,e.price,e.lost_paid AS lostPaid,e.remark,e.supplier_id AS supplierId,COALESCE(s.name,e.supplier_id) AS supplierName,e.lost_status AS lostStatus FROM library_item_entry e LEFT JOIN library_item_category c ON c.itemcat_id=e.item_category AND c.school_id=e.school_id LEFT JOIN library_item_langmaster l ON l.itemlang_id=e.item_language AND l.school_id=e.school_id LEFT JOIN library_item_frequency fr ON fr.itemf_id=e.itemf_id AND fr.school_id=e.school_id LEFT JOIN library_Item_type t ON t.item_type_id=e.item_type AND t.school_id=e.school_id LEFT JOIN library_publisher_entrymaster p ON p.publisher_id=e.publisher_id AND p.school_id=e.school_id LEFT JOIN library_supplier_entry s ON s.supplier_id=e.supplier_id AND s.school_id=e.school_id WHERE e.school_id=?";
        List<Object> args = new ArrayList<>(List.of(school));
        if (!blank(search)) {
            sql += " AND (e.item_acc_no LIKE ? OR e.title LIKE ? OR e.author LIKE ? OR c.name LIKE ? OR p.name LIKE ?)";
            String q = "%" + search.trim() + "%";
            args.addAll(List.of(q, q, q, q, q));
        }
        return jdbc.queryForList(sql + " ORDER BY e.id DESC", args.toArray());
    }

    public Map<String, Object> saveItem(String id, Map<String, Object> b, String school, String session, String staff, boolean update) {
        String acc = text(b.get("accessionNo"));
        if (acc.isBlank()) acc = "LIB" + (jdbc.queryForObject("SELECT ISNULL(MAX(id),0)+1 FROM library_item_entry", Integer.class));
        String sql = "";
        Object[] args;
        if (update) {
            sql = "UPDATE library_item_entry SET lib_entry_date=?,supplier_id=?,item_category=?,title=?,no_of_items=?,serial_no=?,vol_part=?,item_language=?,item_type=?,publisher_id=?,publication_date=?,author=?,keyword=?,isbn_no=?,itemf_id=?,item_classification_no=?,price=?,lost_paid=?,remark=?,subtitle=?,modified_by=?,modified_date=GETDATE() WHERE item_acc_no=? AND school_id=?";
            args = new Object[]{dateOrNull(b.get("libraryEntryDate")), text(b.get("supplierId")), text(b.get("categoryId")), text(b.get("title")), numberOrNull(b.get("totalBooks")), text(b.get("serialNo")), text(b.get("volumePart")), text(b.get("languageId")), text(b.get("typeId")), text(b.get("publisherId")), dateOrNull(b.get("publicationDate")), text(b.get("author")), text(b.get("keyword")), text(b.get("isbnNo")), text(b.get("frequencyId")), text(b.get("classificationNo")), numberOrNull(b.get("price")), numberOrNull(b.get("lostPaid")), text(b.get("remark")), text(b.get("subtitle")), staff, id, school};
        } else {
            sql = "INSERT INTO library_item_entry(item_acc_no,lib_entry_date,supplier_id,item_category,title,no_of_items,serial_no,vol_part,item_language,item_type,publisher_id,publication_date,author,keyword,isbn_no,itemf_id,item_classification_no,price,lost_paid,school_id,remark,lost_status,created_date,created_by,subtitle) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,GETDATE(),?,?)";
            args = new Object[]{acc, dateOrNull(b.get("libraryEntryDate")), text(b.get("supplierId")), text(b.get("categoryId")), text(b.get("title")), numberOrNull(b.get("totalBooks")), text(b.get("serialNo")), text(b.get("volumePart")), text(b.get("languageId")), text(b.get("typeId")), text(b.get("publisherId")), dateOrNull(b.get("publicationDate")), text(b.get("author")), text(b.get("keyword")), text(b.get("isbnNo")), text(b.get("frequencyId")), text(b.get("classificationNo")), numberOrNull(b.get("price")), numberOrNull(b.get("lostPaid")), school, text(b.get("remark")), staff, text(b.get("subtitle"))};
        }
        jdbc.update(sql, args);
        return row("id", acc, "message", update ? "Item updated successfully" : "Item entry saved successfully");
    }

    public void deleteItem(String accession, String school) {
        Integer used = jdbc.queryForObject("SELECT COUNT(1) FROM library_item_issue_return WHERE school_id=? AND item_acc_no=? AND ISNULL(return_status,0)=0", Integer.class, school, accession);
        if (used != null && used > 0) throw new IllegalArgumentException("Issued item cannot be deleted");
        jdbc.update("DELETE FROM library_item_entry WHERE school_id=? AND item_acc_no=?", school, accession);
    }

    public List<Map<String, Object>> students(String school, String session, String secId) {
        String sql = "SELECT st.stu_id AS id,st.stu_id AS studentId,st.registration_no AS registrationNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''))))+'--'+COALESCE(so.father_name,'') AS name,st.sec_id AS secId FROM TB_StudentMaster st JOIN student_other_info so ON so.registration_no=st.registration_no AND so.school_id=st.school_id WHERE st.school_id=? AND st.session=? AND LOWER(st.status)='registered'";
        List<Object> p = new ArrayList<>(List.of(school, session));
        if (!blank(secId) && !"0".equals(secId)) { sql += " AND st.sec_id=?"; p.add(secId); }
        return jdbc.queryForList(sql + " ORDER BY name", p.toArray());
    }

    public List<Map<String, Object>> faculties(String school, String session) {
        return jdbc.queryForList("SELECT staff_u_id AS id,staff_u_id AS facultyId,UPPER(LTRIM(RTRIM(COALESCE(first_name,'')+' '+COALESCE(mid_name,'')+' '+COALESCE(last_name,'')))) AS name,staff_u_id AS memberCode FROM TB_FacultyMaster WHERE school_id=? AND LOWER(ISNULL(status,'')) IN ('active','1','') ORDER BY name", school);
    }

    public List<Map<String, Object>> memberLookup(String type, String code, String school, String session) {
        if ("Faculty".equalsIgnoreCase(type)) return jdbc.queryForList("SELECT TOP 1 staff_u_id AS memberId,staff_u_id AS memberCode,UPPER(LTRIM(RTRIM(COALESCE(first_name,'')+' '+COALESCE(mid_name,'')+' '+COALESCE(last_name,'')))) AS name,'Faculty' AS memberType FROM TB_FacultyMaster WHERE school_id=? AND CONVERT(varchar(50),staff_u_id)=?", school, code);
        return jdbc.queryForList("SELECT TOP 1 st.stu_id AS memberId,st.registration_no AS memberCode,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS name,so.father_name AS fatherName,st.sec_id AS secId,'Student' AS memberType FROM TB_StudentMaster st JOIN student_other_info so ON so.registration_no=st.registration_no AND so.school_id=st.school_id WHERE st.school_id=? AND st.session=? AND (st.registration_no=? OR CONVERT(varchar(50),st.stu_id)=?)", school, session, code, code);
    }

    public List<Map<String, Object>> itemLookup(String accession, String school, String session) {
        return jdbc.queryForList("SELECT TOP 1 e.item_acc_no AS accessionNo,e.title,e.author,e.item_category AS categoryId,COALESCE(c.name,e.item_category) AS categoryName,e.lost_status AS lostStatus,CASE WHEN EXISTS(SELECT 1 FROM library_item_issue_return x WHERE x.school_id=e.school_id AND x.item_acc_no=e.item_acc_no AND ISNULL(x.return_status,0)=0) THEN 1 ELSE 0 END AS currentlyIssued FROM library_item_entry e LEFT JOIN library_item_category c ON c.itemcat_id=e.item_category AND c.school_id=e.school_id WHERE e.school_id=? AND e.item_acc_no=?", school, accession);
    }

    public List<Map<String, Object>> activeIssue(String accession, String school, String session) {
        return jdbc.queryForList(issueBase() + " WHERE i.school_id=? AND i.session=? AND i.item_acc_no=? AND ISNULL(i.return_status,0)=0 ORDER BY i.id DESC", school, session, accession);
    }

    public List<Map<String, Object>> memberIssues(String memberCode, String school, String session) {
        return jdbc.queryForList(issueBase() + " WHERE i.school_id=? AND i.session=? AND i.member_id=? ORDER BY i.id DESC", school, session, memberCode);
    }

    /** Legacy issue_id is a varchar token (not the table's numeric identity id). */
    public String nextIssueId(String school) { return token(); }

    public int returnDays(String school, String memberType, String categoryId, String session) {
        List<Integer> rows = jdbc.query("SELECT TOP 1 CASE WHEN LOWER(?)='student' THEN ISNULL(days_return_lib,7) ELSE ISNULL(days_return_bb,30) END FROM library_items_rule WHERE school_id=? AND session=? AND LOWER(cat_type)=LOWER(?) AND ((','+ISNULL(item_category,'')+',') LIKE '%,'+?+',%' OR ISNULL(item_category,'')='') ORDER BY CASE WHEN item_category=? THEN 0 ELSE 1 END", (rs, n) -> rs.getInt(1), memberType, school, session, memberType, categoryId, categoryId);
        return rows.isEmpty() ? ("Student".equalsIgnoreCase(memberType) ? 7 : 30) : rows.get(0);
    }

    public List<Map<String, Object>> report(String kind, String school, Map<String, String> q) {
        String k = normalize(kind), session = value(q, "session"), memberType = value(q, "memberType"), reportType = value(q, "reportType");
        if ("book-list".equals(k)) return bookList(school, q);
        if ("category".equals(k)) return masterRows("category", school, session);
        if ("stock-in-hand".equals(k)) return jdbc.queryForList("SELECT e.item_acc_no AS accessionNo,e.title,e.author,COALESCE(c.name,e.item_category) AS categoryName,e.no_of_items AS totalBooks,ISNULL(x.issuedBooks,0) AS issuedBooks,ISNULL(x.returnedBooks,0) AS returnedBooks,CASE WHEN ISNULL(e.lost_status,0)=1 THEN 'Lost' WHEN ISNULL(x.issuedBooks,0)>0 THEN 'Issued' ELSE 'Available' END AS stockStatus FROM library_item_entry e LEFT JOIN library_item_category c ON c.itemcat_id=e.item_category AND c.school_id=e.school_id LEFT JOIN (SELECT item_acc_no,school_id,SUM(CASE WHEN ISNULL(return_status,0)=0 THEN 1 ELSE 0 END) issuedBooks,SUM(CASE WHEN ISNULL(return_status,0)=1 THEN 1 ELSE 0 END) returnedBooks FROM library_item_issue_return GROUP BY item_acc_no,school_id) x ON x.item_acc_no=e.item_acc_no AND x.school_id=e.school_id WHERE e.school_id=? ORDER BY e.title,e.item_acc_no", school);
        String base = issueBase();
        StringBuilder sql = new StringBuilder(base).append(" WHERE i.school_id=?");
        List<Object> params = new ArrayList<>(List.of(school));
        if (!session.isBlank()) { sql.append(" AND i.session=?"); params.add(session); }
        if (!memberType.isBlank() && !"all".equalsIgnoreCase(memberType)) { sql.append(" AND i.member_type=?"); params.add(memberType); }
        if ("issue".equalsIgnoreCase(reportType)) sql.append(" AND ISNULL(i.return_status,0)=0");
        if ("return".equalsIgnoreCase(reportType)) sql.append(" AND ISNULL(i.return_status,0)=1");
        if ("defaulter".equals(k)) sql.append(" AND ISNULL(i.return_status,0)=0 AND TRY_CONVERT(date,i.due_date)<CAST(GETDATE() AS date)");
        if ("renew".equals(k)) {
            return jdbc.queryForList("SELECT r.id,r.issue_id AS issueId,r.due_date AS oldDueDate,r.renew_due_date AS renewDueDate,r.created_date AS renewedDate,i.member_id AS memberCode,i.member_type AS memberType,i.item_acc_no AS accessionNo,e.title,e.author FROM library_Item_Renew_Detail r JOIN library_item_issue_return i ON i.issue_id=r.issue_id AND i.school_id=? LEFT JOIN library_item_entry e ON e.item_acc_no=i.item_acc_no AND e.school_id=i.school_id WHERE i.school_id=?" + (session.isBlank() ? "" : " AND i.session=?") + (!memberType.isBlank() && !"all".equalsIgnoreCase(memberType) ? " AND i.member_type=?" : "") + " ORDER BY r.id DESC", renewParams(school, session, memberType).toArray());
        }
        if ("return-details".equals(k)) {
            if (!value(q, "memberCode").isBlank()) { sql.append(" AND i.member_id LIKE ?"); params.add("%" + value(q, "memberCode") + "%"); }
            if (!value(q, "accessionNo").isBlank()) { sql.append(" AND i.item_acc_no LIKE ?"); params.add("%" + value(q, "accessionNo") + "%"); }
        }
        if ("return-date".equals(k)) {
            if (!value(q, "from").isBlank()) { sql.append(" AND TRY_CONVERT(date,i.return_date)>=TRY_CONVERT(date,?)"); params.add(value(q, "from")); }
            if (!value(q, "to").isBlank()) { sql.append(" AND TRY_CONVERT(date,i.return_date)<=TRY_CONVERT(date,?)"); params.add(value(q, "to")); }
        }
        if ("slow-moving".equals(k)) {
            if (!value(q, "accessionNo").isBlank()) { sql.append(" AND i.item_acc_no LIKE ?"); params.add("%" + value(q, "accessionNo") + "%"); }
            sql.append(" AND i.item_acc_no IN (SELECT z.item_acc_no FROM library_item_issue_return z WHERE z.school_id=i.school_id GROUP BY z.item_acc_no HAVING COUNT(*)<=1)");
        }
        sql.append(" ORDER BY i.id DESC");
        return jdbc.queryForList(sql.toString(), params.toArray());
    }

    private List<Map<String, Object>> bookList(String school, Map<String, String> q) {
        String sql = "SELECT e.item_acc_no AS accessionNo,e.title,e.subtitle,e.author,e.keyword,e.isbn_no AS isbnNo,e.price,e.lost_paid AS lostPaid,COALESCE(c.name,e.item_category) AS categoryName,COALESCE(p.name,e.publisher_id) AS publisherName,COALESCE(l.name,e.item_language) AS languageName,COALESCE(fr.name,e.itemf_id) AS frequencyName,e.remark FROM library_item_entry e LEFT JOIN library_item_category c ON c.itemcat_id=e.item_category AND c.school_id=e.school_id LEFT JOIN library_publisher_entrymaster p ON p.publisher_id=e.publisher_id AND p.school_id=e.school_id LEFT JOIN library_item_langmaster l ON l.itemlang_id=e.item_language AND l.school_id=e.school_id LEFT JOIN library_item_frequency fr ON fr.itemf_id=e.itemf_id AND fr.school_id=e.school_id WHERE e.school_id=?";
        List<Object> params = new ArrayList<>(List.of(school));
        if (!blank(q.get("category"))) { sql += " AND e.item_category=?"; params.add(q.get("category")); }
        if (!blank(q.get("author"))) { sql += " AND e.author LIKE ?"; params.add("%" + q.get("author") + "%"); }
        if (!blank(q.get("publisher"))) { sql += " AND e.publisher_id=?"; params.add(q.get("publisher")); }
        if (!blank(q.get("search"))) { sql += " AND (e.item_acc_no LIKE ? OR e.title LIKE ? OR e.author LIKE ? OR c.name LIKE ?)"; String z = "%" + q.get("search") + "%"; params.addAll(List.of(z,z,z,z)); }
        return jdbc.queryForList(sql + " ORDER BY e.title,e.item_acc_no", params.toArray());
    }

    public void insertIssue(Map<String, Object> b, String school, String session, String staff, int days) {
        LocalDate issue = parseDate(text(b.get("issueDate")), LocalDate.now());
        LocalDate due = parseDate(text(b.get("dueDate")), issue.plusDays(days));
        jdbc.update("INSERT INTO library_item_issue_return(issue_id,member_id,item_acc_no,issue_date,due_date,return_date,fine,fine_paid,remark,member_type,created_date,created_by,school_id,session,return_status,renew_status,book_issue_type) VALUES(?,?,?,?,?,NULL,0,0,?,?,GETDATE(),?,?,?,0,0,?)", nextIssueId(school), text(b.get("memberCode")), text(b.get("accessionNo")), Date.valueOf(issue), Date.valueOf(due), text(b.get("remark")), text(b.get("memberType")), staff, school, session, text(b.get("issueType")));
    }

    public void returnIssue(String issueId, Map<String, Object> b, String school, String staff) {
        jdbc.update("UPDATE library_item_issue_return SET return_date=?,fine=?,fine_paid=?,remark=?,return_status=1 WHERE school_id=? AND (CONVERT(varchar(50),issue_id)=? OR item_acc_no=?) AND ISNULL(return_status,0)=0", dateOrNull(b.get("returnDate")), numberOrZero(b.get("fine")), numberOrZero(b.get("finePaid")), text(b.get("remark")), school, issueId, issueId);
    }

    public void renewIssue(String issueId, Map<String, Object> b, String school, String session, String staff, int days) {
        Map<String,Object> current = jdbc.queryForMap("SELECT TOP 1 issue_id,due_date FROM library_item_issue_return WHERE school_id=? AND (CONVERT(varchar(50),issue_id)=? OR item_acc_no=?) AND ISNULL(return_status,0)=0 ORDER BY id DESC", school, issueId, issueId);
        LocalDate oldDue = current.get("due_date") == null ? LocalDate.now() : ((java.sql.Date) current.get("due_date")).toLocalDate();
        LocalDate renewDue = parseDate(text(b.get("renewDueDate")), oldDue.plusDays(days));
        jdbc.update("INSERT INTO library_Item_Renew_Detail(issue_id,due_date,renew_due_date,created_date,created_by,school_id,session) VALUES(?,?,?,GETDATE(),?,?,?)", current.get("issue_id"), Date.valueOf(oldDue), Date.valueOf(renewDue), staff, school, session);
        jdbc.update("UPDATE library_item_issue_return SET due_date=?,renew_status=1 WHERE school_id=? AND issue_id=?", Date.valueOf(renewDue), school, current.get("issue_id"));
    }

    public void lostIssue(Map<String, Object> b, String school, String session, String staff) {
        jdbc.update("UPDATE library_item_entry SET lost_status=1,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND item_acc_no=?", staff, school, text(b.get("accessionNo")));
        jdbc.update("INSERT INTO library_item_lost_details(item_acc_no,member_id,member_type,lost_date,school_id,session,created_by,created_date,remark) VALUES(?,?,?,?,?,?,?,GETDATE(),?)", text(b.get("accessionNo")), text(b.get("memberCode")), text(b.get("memberType")), dateOrNull(b.get("lostDate")), school, session, staff, text(b.get("remark")));
    }

    private String issueBase() {
        return "SELECT DISTINCT i.id,i.issue_id AS issueId,i.member_id AS memberCode,i.member_type AS memberType,i.item_acc_no AS accessionNo,i.issue_date AS issueDate,i.due_date AS dueDate,i.return_date AS returnDate,i.fine,i.fine_paid AS finePaid,i.balance,i.remark,i.return_status AS returnStatus,i.renew_status AS renewStatus,i.session,e.title,e.author,COALESCE(c.name,e.item_category) AS categoryName,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(f.First_name+' '+f.Last_name,'') AS facultyName FROM library_item_issue_return i LEFT JOIN library_item_entry e ON e.item_acc_no=i.item_acc_no AND e.school_id=i.school_id LEFT JOIN library_item_category c ON c.itemcat_id=e.item_category AND c.school_id=e.school_id LEFT JOIN TB_StudentMaster st ON st.registration_no=i.member_id AND st.school_id=i.school_id LEFT JOIN student_other_info so ON so.registration_no=st.registration_no AND so.school_id=st.school_id LEFT JOIN TB_FacultyMaster f ON CONVERT(varchar(50),f.staff_u_id)=i.member_id AND f.school_id=i.school_id";
    }

    private List<Object> renewParams(String school, String session, String type) { List<Object> p = new ArrayList<>(List.of(school, school)); if (!session.isBlank()) p.add(session); if (!type.isBlank() && !"all".equalsIgnoreCase(type)) p.add(type); return p; }
    private Master master(String k) { return switch (k) { case "language" -> new Master("library_item_langmaster","itemlang_id",false); case "frequency" -> new Master("library_item_frequency","itemf_id",false); case "category" -> new Master("library_item_category","itemcat_id",true); case "publisher-category" -> new Master("library_publisher_catmaster","pubcat_id",false); case "supplier-category" -> new Master("library_sup_category","supcat_id",false); default -> throw new IllegalArgumentException("Unsupported Library master: " + k); }; }
    private record Master(String table, String idColumn, boolean unit) { }

    public static String normalize(String x) { return text(x).toLowerCase(Locale.ROOT).replace('_','-').replace(' ','-'); }
    public static boolean blank(String x) { return x == null || x.trim().isEmpty(); }
    public static String text(Object x) { return x == null ? "" : String.valueOf(x).trim(); }
    private static String upper(String x) { return x == null ? "" : x.trim().toUpperCase(Locale.ROOT); }
    private static String token() { return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT); }
    private static Map<String,Object> row(Object... x) { Map<String,Object> r = new LinkedHashMap<>(); for (int i=0;i+1<x.length;i+=2) r.put(String.valueOf(x[i]),x[i+1]); return r; }
    private static String value(Map<String,String> q, String k) { return text(q.get(k)); }
    private static Object numberOrNull(Object x) { return blank(text(x)) ? null : Double.valueOf(text(x)); }
    private static Object numberOrZero(Object x) { return blank(text(x)) ? 0d : Double.valueOf(text(x)); }
    private static int integer(Object x) { try { return blank(text(x)) ? 0 : Integer.parseInt(text(x)); } catch (Exception e) { return 0; } }
    private static List<String> strings(Object raw) { List<String> out = new ArrayList<>(); if (raw instanceof Collection<?> c) for (Object x : c) if (!text(x).isBlank()) out.add(text(x)); else if (raw != null && !text(raw).isBlank()) out.add(text(raw)); return out; }
    private static Object dateOrNull(Object x) { String s=text(x); return s.isBlank() ? null : Date.valueOf(parseDate(s, LocalDate.now())); }
    private static LocalDate parseDate(String s, LocalDate fallback) { if (blank(s)) return fallback; try { return LocalDate.parse(s.substring(0, Math.min(10,s.length()))); } catch (Exception e) { return fallback; } }
}
