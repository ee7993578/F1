package com.edufox.schoolerp.master;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class MasterSettingRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap(String schoolId, String staffUid) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("classes", classes(schoolId));
        out.put("sections", sections(schoolId));
        out.put("designations", designations(schoolId));
        out.put("staff", staff(schoolId));
        out.put("modules", modules(schoolId));
        out.put("moduleMappings", moduleMappings(schoolId, currentSession(schoolId)));
        out.put("pages", pages(schoolId));
        out.put("sessions", sessions(schoolId));
        out.put("terms", terms(schoolId, currentSession(schoolId)));
        out.put("termOptions", jdbc.queryForList("SELECT term_id AS id, term_name AS name FROM term ORDER BY term_id"));
        out.put("years", yearOptions());
        out.put("logs", logs(schoolId, null, null, null, null));
        out.put("profile", profile(schoolId, staffUid));
        out.put("currentSession", currentSession(schoolId));
        return out;
    }

    public String currentSession(String schoolId) {
        List<String> values = jdbc.query(
            "SELECT TOP (1) session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC",
            (rs, row) -> rs.getString(1), schoolId);
        return values.isEmpty() ? "" : Objects.toString(values.get(0), "");
    }

    public List<Map<String, Object>> classes(String schoolId) {
        return jdbc.queryForList("""
            SELECT id AS classId, class_name AS className, class_group AS classGroup,
                   class_order AS classOrder, displayClassName AS displayName
            FROM class_name
            WHERE school_id=?
            ORDER BY COALESCE(class_order, 9999), id
            """, schoolId);
    }

    public List<Map<String, Object>> sections(String schoolId) {
        return jdbc.queryForList("""
            SELECT c.Sec_id AS sectionId, c.class_id AS classId, c.Section AS sectionName,
                   COALESCE(n.class_name,'') AS className,
                   COALESCE(c.ClassType,'Regular') AS sectionType
            FROM TB_Class c
            LEFT JOIN class_name n ON n.id=c.class_id AND n.school_id=c.school_id
            WHERE c.school_id=?
            ORDER BY n.id, c.Section, c.Sec_id
            """, schoolId);
    }

    public List<Map<String, Object>> designations(String schoolId) {
        return jdbc.queryForList("""
            SELECT Desg_Id AS id, Design_Name AS name, Design_Code AS code,
                   COALESCE(DisplayName, Design_Name) AS displayName,
                   COALESCE(Desig_Order, 9999) AS displayOrder,
                   Status AS status, SystemGenerated AS systemGenerated,
                   Desig_Type AS designationType
            FROM TB_Desig
            WHERE school_id=?
            ORDER BY COALESCE(Desig_Order,9999), Design_Name
            """, schoolId);
    }

    public List<Map<String, Object>> staff(String schoolId) {
        return jdbc.queryForList("""
            SELECT f.f_id AS facultyId, f.staff_u_id AS staffUid,
                   LTRIM(RTRIM(CONCAT(ISNULL(f.First_name,''),' ',
                                      ISNULL(f.Mid_name,''),' ',
                                      ISNULL(f.Last_name,'')))) AS name,
                   f.designation AS designationId,
                   COALESCE(d.Design_Name,'') AS designationName,
                   f.Department AS department, f.Mobile AS mobile,
                   f.status AS status
            FROM TB_FacultyMaster f
            LEFT JOIN TB_Desig d ON d.Desg_Id=f.designation AND d.school_id=f.school_id
            WHERE f.school_id=? AND ISNULL(f.status,'') NOT IN ('Withdrawn','Deleted')
            ORDER BY name, f.f_id
            """, schoolId);
    }

    public List<Map<String, Object>> modules(String schoolId) {
        return jdbc.queryForList("""
            SELECT m.module_id AS moduleId, m.module_name AS moduleName,
                   m.image_path AS icon, m.priority AS priority,
                   CASE WHEN smm.id IS NULL OR smm.status=0 THEN CAST(0 AS bit)
                        ELSE CAST(1 AS bit) END AS mapped,
                   COALESCE(smm.RedirectToOtherSoftware,'') AS redirectUrl
            FROM module_master_efox m
            LEFT JOIN school_module_mapping_efox smm
              ON smm.module_id=m.module_id AND smm.school_id=? AND smm.status=1
            WHERE m.status=1
            ORDER BY TRY_CONVERT(int,m.priority), m.module_id
            """, schoolId);
    }

    public List<Map<String, Object>> pages(String schoolId) {
        return jdbc.queryForList("""
            SELECT p.PageId AS pageId, p.newpageId AS newPageId, p.module_id AS moduleId,
                   p.Page_Name AS pageName, p.Pageurl AS pageUrl, p.Priority AS priority,
                   p.sub_module_id AS subModuleId, p.parent_id AS parentId,
                   p.type AS pageType, p.icon AS icon,
                   COALESCE(ps.PageDisplayName,p.Page_Name) AS displayName,
                   COALESCE(ps.PagePriority,p.Priority) AS displayPriority,
                   CASE WHEN ps.id IS NULL THEN CAST(0 AS bit) ELSE CAST(1 AS bit) END AS mapped
            FROM page_master_efox p
            LEFT JOIN PageSchoolMapping_efox ps
              ON ps.PageId=p.newpageId AND ps.ModuleId=p.module_id
             AND ps.School_id=? AND ps.Status=1
            WHERE p.status=1
            ORDER BY p.module_id, COALESCE(ps.PagePriority,p.Priority), p.PageId
            """, schoolId);
    }

    public List<Map<String, Object>> sessions(String schoolId) {
        return jdbc.queryForList("""
            SELECT id, session, status, Session_Month_Start AS monthStart,
                   Session_year_start AS yearStart, Session_Month_End AS monthEnd,
                   Session_Year_End AS yearEnd, Priority AS priority
            FROM TB_Session
            WHERE school_id=?
            ORDER BY Session_Year_Start DESC, id DESC
            """, schoolId);
    }

    public List<Map<String, Object>> yearOptions() {
        List<Map<String, Object>> years = new ArrayList<>();
        int current = java.time.Year.now().getValue();
        for (int year = current - 3; year <= current + 3; year++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("value", String.valueOf(year));
            item.put("label", year + "-" + (year + 1));
            years.add(item);
        }
        return years;
    }

    public List<Map<String, Object>> terms(String schoolId, String session) {
        if (session == null || session.isBlank()) return List.of();
        return jdbc.queryForList("""
            SELECT st.s_no AS id, st.term AS term, st.class_id AS classId,
                   COALESCE(c.class_name,'') AS className,
                   CONVERT(varchar(10),st.startdate,23) AS startDate,
                   CONVERT(varchar(10),st.enddate,23) AS endDate,
                   st.Session AS session
            FROM session_term st
            LEFT JOIN class_name c ON c.id=st.class_id AND c.school_id=st.school_id
            WHERE st.school_id=? AND st.Session=?
            ORDER BY c.class_order, st.class_id, st.term
            """, schoolId, session);
    }

    public List<Map<String, Object>> logs(String schoolId, String from, String to, String page, String activity) {
        StringBuilder sql = new StringBuilder("""
            SELECT TOP (500) id AS id, activity_type AS activityType,
                   created_on AS createdOn, full_page_name AS fullPageName,
                   ip_address AS ipAddress, login_id AS loginId,
                   page_name AS pageName, staff_u_id AS staffUid
            FROM activity_log
            WHERE school_id=?
            """);
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        if (from != null && !from.isBlank()) { sql.append(" AND CAST(created_on AS date)>=?"); args.add(from); }
        if (to != null && !to.isBlank()) { sql.append(" AND CAST(created_on AS date)<=?"); args.add(to); }
        if (page != null && !page.isBlank()) { sql.append(" AND page_name LIKE ?"); args.add("%" + page + "%"); }
        if (activity != null && !activity.isBlank()) { sql.append(" AND activity_type=?"); args.add(activity); }
        sql.append(" ORDER BY created_on DESC, id DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public Map<String, Object> profile(String schoolId, String staffUid) {
        List<Map<String, Object>> values = jdbc.queryForList("""
            SELECT TOP (1) f.f_id AS facultyId, f.staff_u_id AS staffUid,
              f.Faculty_id AS facultyCode, f.First_name AS firstName, f.Mid_name AS middleName,
              f.Last_name AS lastName, f.user_email AS email, f.Phone AS phone,
              f.Mobile AS mobile, f.E_mail AS alternateEmail, f.DOB AS dob,
              f.FH_Name AS fatherName, f.Gender AS gender, f.M_status AS maritalStatus,
              f.R_Address AS residentialAddress, f.Present_Address AS presentAddress,
              f.Pin AS pin, f.District_id AS districtId, f.City AS city,
              f.State_id AS stateId, f.Blood_gp AS bloodGroup, f.BQualification AS bachelorQualification,
              f.HQualification AS higherQualification, f.Department AS department,
              f.designation AS designationId, f.Image_path AS imagePath,
              COALESCE(d.Design_Name,'') AS designationName
            FROM TB_FacultyMaster f
            LEFT JOIN TB_Desig d ON d.Desg_Id=f.designation AND d.school_id=f.school_id
            WHERE f.school_id=? AND CAST(f.staff_u_id AS varchar(50))=?
            """, schoolId, staffUid);
        return values.isEmpty() ? Map.of() : values.get(0);
    }

    public List<Map<String, Object>> permissionConfig(String schoolId, Integer designationId) {
        return jdbc.queryForList("""
            SELECT p.PageId AS pageId, p.newpageId AS newPageId, p.module_id AS moduleId,
                   p.Page_Name AS pageName, p.Pageurl AS pageUrl,
                   COALESCE(rp.ViewRole,0) AS viewRole, COALESCE(rp.AddRole,0) AS addRole,
                   COALESCE(rp.UpdateRole,0) AS updateRole, COALESCE(rp.DeleteRole,0) AS deleteRole,
                   COALESCE(rp.class_id,'') AS classIds
            FROM page_master_efox p
            LEFT JOIN Roles_Permission_efox rp
              ON rp.pageid=p.PageId AND rp.module_id=p.module_id
             AND rp.Desi_id=? AND rp.school_id=? AND rp.Status=1
            WHERE p.status=1
            ORDER BY p.module_id, p.Priority, p.PageId
            """, designationId, schoolId);
    }

    public List<Map<String, Object>> moduleMappings(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT am.id AS id, am.module_id AS moduleId, am.class_id AS classId,
                   am.SessionID AS sessionId, COALESCE(m.module_name,'') AS moduleName,
                   COALESCE(c.class_name,'') AS className
            FROM assign_module_classwise am
            LEFT JOIN module_master_efox m ON TRY_CONVERT(int,am.module_id)=m.module_id
            LEFT JOIN class_name c ON TRY_CONVERT(int,am.class_id)=c.id AND c.school_id=am.school_id
            WHERE am.school_id=? AND am.status='1' AND (am.SessionID=? OR ISNULL(am.SessionID,'')='')
            ORDER BY m.priority, m.module_name, c.class_order, c.class_name
            """, schoolId, session);
    }

    @Transactional
    public void insertClass(String schoolId, String staffUid, String className, String classGroup) {
        Integer nextId = jdbc.queryForObject(
            "SELECT ISNULL(MAX(id),0)+1 FROM class_name WITH (UPDLOCK,HOLDLOCK) WHERE school_id=?",
            Integer.class, schoolId);
        jdbc.update("""
            INSERT INTO class_name(id,class_name,created_date,created_by,school_id,class_group,class_order)
            VALUES(?,?,GETDATE(),?,?,?,?)
            """, nextId, className.trim().toUpperCase(Locale.ROOT), staffUid, schoolId, classGroup,
            nextId);
    }

    @Transactional
    public void insertSection(String schoolId, String staffUid, List<Integer> classIds,
                               String sectionName, String sectionType) {
        if (classIds == null || classIds.isEmpty()) return;
        Integer next = jdbc.queryForObject(
            "SELECT ISNULL(MAX(Sec_id),0)+1 FROM TB_Class WITH (UPDLOCK,HOLDLOCK) WHERE school_id=?",
            Integer.class, schoolId);
        for (Integer classId : classIds) {
            jdbc.update("""
                INSERT INTO TB_Class(Sec_id,Section,class_id,created_date,created_by,school_id,ClassType)
                VALUES(?,?,?,GETDATE(),?,?,?)
                """, next++, sectionName.trim().toUpperCase(Locale.ROOT), classId,
                staffUid, schoolId, sectionType == null ? "Regular" : sectionType);
        }
    }

    public int updateSection(String schoolId, int sectionId, String sectionName, String sectionType) {
        return jdbc.update("""
            UPDATE TB_Class SET Section=?, ClassType=COALESCE(?,ClassType),
                   modified_date=GETDATE(), modified_by=?
            WHERE Sec_id=? AND school_id=?
            """, sectionName.trim().toUpperCase(Locale.ROOT), sectionType, "new-project",
            sectionId, schoolId);
    }

    public int updateDesignation(String schoolId, int id, String code, String displayName, int order, String staffUid) {
        return jdbc.update("""
            UPDATE TB_Desig SET Design_Code=?, DisplayName=?, Desig_Order=?,
                   Modified_By=?, Modified_Date=CAST(GETDATE() AS date)
            WHERE Desg_Id=? AND school_id=?
            """, code.trim().toUpperCase(Locale.ROOT), displayName.trim(), order, staffUid, id, schoolId);
    }

    public void insertDesignation(String schoolId, String staffUid, String name, String code) {
        Integer next = jdbc.queryForObject(
            "SELECT ISNULL(MAX(Desg_Id),0)+1 FROM TB_Desig WITH (UPDLOCK,HOLDLOCK) WHERE school_id=?",
            Integer.class, schoolId);
        jdbc.update("""
            INSERT INTO TB_Desig(Desg_Id,Design_Name,Design_Code,Status,Created_By,Created_Date,school_id,SystemGenerated)
            VALUES(?,?,?,'1',?,CAST(GETDATE() AS date),?,0)
            """, next, name.trim(), code == null ? "" : code.trim().toUpperCase(Locale.ROOT), staffUid, schoolId);
    }

    public void insertSession(String schoolId, String staffUid, String session) {
        String[] years = session.trim().split("[-/]");
        int start = Integer.parseInt(years[0].trim());
        int end = years.length > 1 ? Integer.parseInt(years[1].trim()) : start + 1;
        jdbc.update("""
            INSERT INTO TB_Session(Session_Month_Start,Session_year_start,Session_Month_End,
              Session_Year_End,status,school_id,session,Priority,LoginSet)
            VALUES('April',?, 'March',?,'Not Set',?,?,?, '0')
            """, start, end, schoolId, start + "-" + end, String.valueOf(start));
    }

    @Transactional
    public void setDefaultSession(String schoolId, int id) {
        jdbc.update("UPDATE TB_Session SET status='Not Set' WHERE school_id=?", schoolId);
        jdbc.update("UPDATE TB_Session SET status='Default' WHERE id=? AND school_id=?", id, schoolId);
    }

    @Transactional
    public void saveTerm(String schoolId, String staffUid, MasterSettingDtos.TermSaveRequest request,
                         String session) {
        if (request.id() == null) {
            jdbc.update("""
                INSERT INTO session_term(term,startdate,enddate,created_date,created_by,modified_date,
                  modified_by,school_id,Session,class_id)
                VALUES(?,?,?,GETDATE(),?,GETDATE(),?,?,?,?)
                """, request.term(), request.startDate(), request.endDate(), staffUid, staffUid,
                schoolId, session, request.classId());
        } else {
            jdbc.update("""
                UPDATE session_term SET term=?, startdate=?, enddate=?, modified_date=GETDATE(),
                   modified_by=?, class_id=?
                WHERE s_no=? AND school_id=? AND Session=?
                """, request.term(), request.startDate(), request.endDate(), staffUid,
                request.classId(), request.id(), schoolId, session);
        }
    }

    @Transactional
    public void saveModuleMapping(String schoolId, String staffUid, MasterSettingDtos.ModuleMappingRequest request,
                                  String session) {
        jdbc.update("UPDATE assign_module_classwise SET status='0' WHERE module_id=? AND school_id=? AND (SessionID=? OR ISNULL(SessionID,'')='')",
            request.moduleId(), schoolId, session);
        for (String classId : request.classIds()) {
            int count = jdbc.update("""
                UPDATE assign_module_classwise SET status='1', created_by=?, created_date=?, SessionID=?
                WHERE module_id=? AND class_id=? AND school_id=?
                """, staffUid, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                session, request.moduleId(), classId, schoolId);
            if (count == 0) {
                jdbc.update("""
                    INSERT INTO assign_module_classwise(module_id,class_id,status,created_by,created_date,school_id,SessionID)
                    VALUES(?,?, '1', ?, ?, ?, ?)
                    """, request.moduleId(), classId, staffUid,
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    schoolId, session);
            }
        }
    }

    @Transactional
    public void savePermissions(String schoolId, String staffUid, MasterSettingDtos.PermissionSaveRequest request) {
        jdbc.update("UPDATE Roles_Permission_efox SET Status=0 WHERE Desi_id=? AND school_id=?",
            request.designationId(), schoolId);
        Integer staffId = parseInt(staffUid);
        for (MasterSettingDtos.PermissionRow p : request.permissions()) {
            if (!p.view() && !p.add() && !p.update() && !p.delete()) continue;
            jdbc.update("""
                INSERT INTO Roles_Permission_efox(Desi_id,pageid,module_id,ViewRole,AddRole,UpdateRole,DeleteRole,
                  ImportRole,ExportRole,PrintRole,created_by,CreateDate,Status,Staff_U_id,school_id,class_id)
                VALUES(?,?,?,?,?,?,?,0,0,0,?,GETDATE(),1,?,?,?)
                """, request.designationId(), p.pageId(), p.moduleId(), yes(p.view()), yes(p.add()),
                yes(p.update()), yes(p.delete()), staffUid, staffId, schoolId,
                p.classIds() == null ? "" : p.classIds());
        }
    }

    public int updatePassword(String schoolId, String staffUid, String oldPassword, String newPassword) {
        return jdbc.update("""
            UPDATE TB_FacultyMaster SET Password=?, modified_date=GETDATE()
            WHERE school_id=? AND CAST(staff_u_id AS varchar(50))=? AND Password=?
            """, newPassword, schoolId, staffUid, oldPassword);
    }

    public int updateProfile(String schoolId, String staffUid, Map<String, Object> fields) {
        Map<String, String> allowed = Map.ofEntries(
            Map.entry("firstName","First_name"), Map.entry("middleName","Mid_name"),
            Map.entry("lastName","Last_name"), Map.entry("email","user_email"),
            Map.entry("phone","Phone"), Map.entry("mobile","Mobile"), Map.entry("gender","Gender"),
            Map.entry("maritalStatus","M_status"), Map.entry("residentialAddress","R_Address"),
            Map.entry("presentAddress","Present_Address"), Map.entry("pin","Pin"),
            Map.entry("city","City"), Map.entry("department","Department")
        );
        List<String> sets = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        fields.forEach((key, value) -> {
            String column = allowed.get(key);
            if (column != null) { sets.add(column + "=?"); args.add(value == null ? null : String.valueOf(value)); }
        });
        if (sets.isEmpty()) return 0;
        sets.add("modified_date=GETDATE()");
        args.add(schoolId); args.add(staffUid);
        return jdbc.update("UPDATE TB_FacultyMaster SET " + String.join(",", sets) +
            " WHERE school_id=? AND CAST(staff_u_id AS varchar(50))=?", args.toArray());
    }

    private static int yes(boolean value) { return value ? 1 : 0; }
    private static Integer parseInt(String value) {
        try { return Integer.valueOf(value); } catch (Exception ignored) { return null; }
    }
}
