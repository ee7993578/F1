package com.edufox.schoolerp.superadmin;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SuperAdminRepository {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("states", jdbc.queryForList("SELECT State_Id AS id, COALESCE(NULLIF(StateDisplayName,''),State_Name) AS name FROM state ORDER BY name"));
        result.put("modules", moduleList(""));
        result.put("roles", roleList(""));
        result.put("schools", jdbc.queryForList("SELECT TOP 100 school_id AS schoolId,schoolname AS schoolName,status FROM schoolmaster ORDER BY schoolname"));
        return result;
    }

    public Map<String, Object> school(String schoolId) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> schools = jdbc.queryForList("""
            SELECT TOP 1 s.school_id AS schoolId,s.schoolname AS schoolName,COALESCE(s.WebsiteUrl,'') AS websiteUrl,
                   COALESCE(s.affiliation_no,'') AS affiliationNo,COALESCE(s.school_code,'') AS schoolCode,
                   COALESCE(s.phone,'') AS phone,COALESCE(s.mobile,'') AS mobile,COALESCE(s.[e-mail],'') AS email,
                   COALESCE(s.address,'') AS address,COALESCE(s.Pincode,'') AS pincode,s.state_id AS stateId,s.city_id AS cityId,
                   COALESCE(s.logo_path,'') AS logoPath,COALESCE(st.StateDisplayName,st.State_Name,'') AS stateName,
                   COALESCE(d.City_Name,'') AS cityName
            FROM schoolmaster s LEFT JOIN state st ON st.State_Id=s.state_id LEFT JOIN Districts d ON d.City_Id=s.city_id
            WHERE s.school_id=?
            """, schoolId);
        result.put("school", schools.isEmpty() ? Map.of() : schools.get(0));
        result.put("modules", moduleList(schoolId));
        result.put("roles", roleList(schoolId));
        return result;
    }

    private List<Map<String, Object>> moduleList(String schoolId) {
        return jdbc.queryForList("""
            SELECT m.module_id AS id,m.module_name AS name,COALESCE(m.IsDefault,0) AS isDefault,
                   CASE WHEN COALESCE(m.IsDefault,0)=1 OR sm.module_id IS NOT NULL THEN 1 ELSE 0 END AS selected,
                   CASE WHEN COALESCE(m.IsDefault,0)=1 THEN 0 ELSE 1 END AS enabled
            FROM module_master_efox m
            LEFT JOIN school_module_mapping_efox sm ON sm.module_id=m.module_id AND sm.school_id=? AND sm.status=1
            WHERE m.status=1 ORDER BY COALESCE(m.IsDefault,0) DESC,TRY_CONVERT(int,m.priority),m.module_name
            """, schoolId == null ? "" : schoolId);
    }

    private List<Map<String, Object>> roleList(String schoolId) {
        return jdbc.queryForList("""
            SELECT d.desig_id AS id,d.desig_name AS name,1 AS selected,0 AS enabled,d.SystemGenerated AS systemGenerated
            FROM designation d WHERE d.status=1 AND d.SystemGenerated=1
            UNION ALL
            SELECT dg.Desg_Id AS id,dg.Design_Name AS name,1 AS selected,1 AS enabled,d.SystemGenerated AS systemGenerated
            FROM TB_Desig dg JOIN designation d ON d.desig_id=dg.Desg_Id
            WHERE dg.school_id=? AND dg.status='active' AND d.status=1 AND d.SystemGenerated=0
            ORDER BY name
            """, schoolId == null ? "" : schoolId);
    }

    public List<Map<String, Object>> cities(Integer stateId) {
        return jdbc.queryForList("SELECT City_Id AS id,City_Name AS name,state_id AS stateId FROM Districts WHERE (? IS NULL OR state_id=?) ORDER BY City_Name", stateId, stateId);
    }

    public Map<String, Object> pageMappingBootstrap() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("schools", jdbc.queryForList("""
            SELECT school_id AS id,schoolname AS name,status
            FROM schoolmaster
            WHERE ISNULL(status,1)=1
            ORDER BY schoolname,school_id
            """));
        result.put("modules", List.of());
        return result;
    }

    public List<Map<String, Object>> pageMappingModules(String schoolId) {
        return jdbc.queryForList("""
            SELECT DISTINCT m.module_id AS id,m.module_name AS name,
                   TRY_CONVERT(int,m.priority) AS priority,
                   CASE WHEN EXISTS (
                       SELECT 1 FROM Roles_Permission_efox rp
                       WHERE rp.school_id=? AND rp.module_id=m.module_id AND rp.status=1
                   ) THEN 1 ELSE 0 END AS mapped
            FROM module_master_efox m
            JOIN school_module_mapping_efox sm
              ON sm.module_id=m.module_id AND sm.school_id=? AND sm.status=1
            WHERE m.status=1
            ORDER BY TRY_CONVERT(int,m.priority),m.module_id
            """, schoolId, schoolId);
    }

    public List<Map<String, Object>> pageMappingPages(String schoolId, int moduleId) {
        return jdbc.queryForList("""
            SELECT DISTINCT p.PageId AS pageId,p.NewpageId AS newPageId,p.module_id AS moduleId,
                   p.Page_Name AS pageName,p.Pageurl AS pageUrl,p.Priority AS priority,
                   p.sub_module_id AS subModuleId,p.parent_id AS parentId,p.type AS pageType,
                   COALESCE(NULLIF(ps.PageDisplayName,''),p.Page_Name) AS displayName,
                   COALESCE(ps.PagePriority,p.Priority) AS displayPriority,
                   CASE WHEN ps.id IS NULL OR ps.Status<>1 THEN 0 ELSE 1 END AS checked,
                   COALESCE(NULLIF(parentPage.Page_Name,''),
                       CASE WHEN LOWER(ISNULL(p.type,''))='reports' THEN 'Reports' ELSE 'Other' END) AS parentName
            FROM PageSchoolMapping_efox ps
            RIGHT JOIN page_master_efox p
              ON ps.pageid=p.newpageid AND ps.school_id=? AND ps.ModuleId=?
            LEFT JOIN page_master_efox parentPage
              ON parentPage.NewpageId=TRY_CONVERT(int,NULLIF(p.parent_id,''))
            WHERE p.module_id=? AND p.status=1
              AND p.PageId IN (SELECT DISTINCT NewpageId FROM page_master_efox WHERE NewpageId IS NOT NULL)
            ORDER BY COALESCE(ps.PagePriority,p.Priority),p.Page_Name,p.PageId
            """, schoolId, moduleId, moduleId);
    }

    @Transactional
    public Map<String, Object> savePageMappings(SuperAdminDtos.PageMappingSaveRequest request) {
        String schoolId = text(request.schoolId());
        int moduleId = request.moduleId() == null ? 0 : request.moduleId();
        if (schoolId.isBlank() || moduleId <= 0) return Map.of("success", false, "message", "School and module are required");
        if (count("SELECT COUNT(*) FROM schoolmaster WHERE school_id=? AND ISNULL(status,1)=1", schoolId) == 0)
            return Map.of("success", false, "message", "School not found");
        if (count("SELECT COUNT(*) FROM module_master_efox WHERE module_id=? AND status=1", moduleId) == 0)
            return Map.of("success", false, "message", "Module not found");

        // Same old-page behavior: first deactivate every page mapping for the
        // selected school/module, then restore only the checked rows.
        jdbc.update("""
            UPDATE PageSchoolMapping_efox
            SET Status=0,Modified_Date=GETDATE(),Modified_By='SUPERADMIN'
            WHERE School_id=? AND ModuleId=?
            """, schoolId, moduleId);

        List<SuperAdminDtos.PageMappingRow> pages = request.pages() == null ? List.of() : request.pages();
        int selected = 0;
        for (SuperAdminDtos.PageMappingRow page : pages) {
            if (page == null || page.pageId() == null) continue;
            if (!page.checked()) {
                jdbc.update("UPDATE Roles_Permission_efox SET status=0 WHERE pageid=? AND school_id=?", page.pageId(), schoolId);
                jdbc.update("DELETE FROM PageSchoolMapping_efox WHERE pageid=? AND school_id=?", page.pageId(), schoolId);
                continue;
            }
            selected++;
            String displayName = text(page.displayName());
            int priority = page.priority() == null ? 0 : page.priority();
            int updated = jdbc.update("""
                UPDATE PageSchoolMapping_efox
                SET Status=1,PageDisplayName=?,PagePriority=?,Modified_Date=GETDATE(),Modified_By='SUPERADMIN'
                WHERE PageId=? AND School_id=? AND ModuleId=?
                """, displayName, priority, page.pageId(), schoolId, moduleId);
            if (updated == 0 && count("SELECT COUNT(*) FROM PageSchoolMapping_efox WHERE PageId=? AND School_id=?", page.pageId(), schoolId) == 0) {
                jdbc.update("""
                    INSERT INTO PageSchoolMapping_efox
                        (PageId,School_id,PageDisplayName,PagePriority,Status,Created_Date,Created_By,Modified_Date,Modified_By,ModuleId)
                    VALUES(?,?,?, ?,1,GETDATE(),'SUPERADMIN',GETDATE(),'SUPERADMIN',?)
                    """, page.pageId(), schoolId, displayName, priority, moduleId);
            }
        }
        return Map.of("success", true, "message", "Page mapping saved", "selected", selected);
    }

    @Transactional
    public Map<String, Object> saveSchool(SuperAdminDtos.SchoolRequest request) {
        String requestedId = text(request.schoolId());
        String schoolId = requestedId;
        if (schoolId.isBlank()) {
            if (count("SELECT COUNT(*) FROM schoolmaster WHERE LOWER(schoolname)=LOWER(?)", request.schoolName()) > 0)
                return Map.of("success", false, "message", "School already exists");
            schoolId = generateSchoolId(request.schoolName());
            insertNewSchool(schoolId, request);
            return Map.of("success", true, "schoolId", schoolId, "message", "School created successfully");
        }
        if (count("SELECT COUNT(*) FROM schoolmaster WHERE LOWER(schoolname)=LOWER(?) AND school_id<>?", request.schoolName(), schoolId) > 0)
            return Map.of("success", false, "message", "Another school already uses this name");
        int updated = jdbc.update("""
            UPDATE schoolmaster SET schoolname=?,loginpage_schoolname=?,address=?,phone=?,mobile=?,[e-mail]=?,
                affiliation_no=?,school_code=?,WebsiteUrl=?,state_id=?,city_id=?,Pincode=?
            WHERE school_id=?
            """, request.schoolName(), request.schoolName(), request.address(), request.phone(), request.mobile(), request.email(),
            request.affiliationNo(), request.schoolCode(), request.websiteUrl(), request.stateId(), request.cityId(), request.pincode(), schoolId);
        if (updated == 0) return Map.of("success", false, "message", "School not found");
        if (!text(request.logoPath()).isBlank()) jdbc.update("UPDATE schoolmaster SET logo_path=? WHERE school_id=?", request.logoPath(), schoolId);
        return Map.of("success", true, "schoolId", schoolId, "message", "School updated successfully");
    }

    private void insertNewSchool(String schoolId, SuperAdminDtos.SchoolRequest request) {
        jdbc.update("""
            INSERT INTO schoolmaster(school_id,schoolname,loginpage_schoolname,address,phone,mobile,[e-mail],logo_path,
                affiliation_no,school_code,WebsiteUrl,state_id,city_id,Pincode,status,campusid,Software_Domain,web_address)
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?, ?,1,?,?,?)
            """, schoolId, request.schoolName(), request.schoolName(), request.address(), request.phone(), request.mobile(), request.email(),
            text(request.logoPath()), request.affiliationNo(), request.schoolCode(), request.websiteUrl(), request.stateId(), request.cityId(), request.pincode(),
            "campus@" + schoolId, "https://edufox.efoxtechnologies.com/", "https://edufox.efoxtechnologies.com/");
        jdbc.update("""
            INSERT INTO configuration(school_id,considerTransport,considerHostel,create_date,create_by,barcode,attendance_type,
                colorScheme,att_sms,GenerateFeeReceiptAfterDeposit,onlinePaymentStatusJobRegistration,ConsiderLateFees_PreviousSession,
                back_date_deposit,addLateFeeWithPrviousDue,otp_based,lateFeeApplicableType,marks_mapping_type)
            VALUES(?,?,?,GETDATE(),?,?,?,?,?,?,?,?,?,?,?,?,?)
            """, schoolId, false, false, "1", false, 1, "themes/lightblue_admin.css", false, 1, false, false, "0", false, "0", "installmentwise", 2);
        jdbc.update("INSERT INTO UserStatus(UserType,Status,school_id,IsActive,create_date,created_by,Priority) VALUES('Student','Registered',?,1,GETDATE(),'1',1)", schoolId);
        jdbc.update("INSERT INTO UserStatus(UserType,Status,school_id,IsActive,create_date,created_by,Priority) VALUES('Student','Withdrawn',?,1,GETDATE(),'1',2)", schoolId);
        int year = java.time.Year.now().getValue();
        String session = year + " - " + (year + 1);
        jdbc.update("INSERT INTO tb_session(Session_Month_Start,Session_year_start,Session_Month_End,Session_Year_End,status,school_id,session) VALUES('January',?,'January',?,'Default',?,?)", year, year + 1, schoolId, session);
        jdbc.update("INSERT INTO tb_facultymaster(staff_u_id,F_id,Password,First_name,Mid_name,Last_name,Mobile,Joining_date,status,school_id,Registration_date,Image_path) VALUES('1','1',?,?,?,?,CAST(GETDATE() AS date),1,?,GETDATE(),'/images/dummy_image.jpg')", "admin@" + schoolId, "Admin", "", "", request.mobile(), schoolId);
        jdbc.update("INSERT INTO tb_faculty_withdrawn(f_id,status,w_date,Remark,created_date,created_by,modified_date,modified_by,school_id,session,staff_u_id) VALUES(1,1,'1900-01-01','',GETDATE(),'','1900-01-01','',?,?,1)", schoolId, session);
        jdbc.update("INSERT INTO desig_mapping(Desg_id,Staff_U_id,f_id,school_id) VALUES(28,'1',1,?)", schoolId);
    }

    @Transactional
    public Map<String, Object> saveModules(String schoolId, SuperAdminDtos.SelectionRequest request) {
        List<Integer> ids = cleanIds(request == null ? null : request.ids());
        for (Integer id : ids) {
            jdbc.update("""
                INSERT INTO school_module_mapping_efox(module_id,status,created_date,created_by,school_id)
                SELECT ?,1,GETDATE(),'SUPERADMIN',? WHERE NOT EXISTS
                    (SELECT 1 FROM school_module_mapping_efox WHERE module_id=? AND school_id=? AND status=1)
                """, id, schoolId, id, schoolId);
        }
        if (ids.isEmpty()) jdbc.update("UPDATE school_module_mapping_efox SET status=0,modified_date=GETDATE(),modified_by='SUPERADMIN' WHERE school_id=?", schoolId);
        else jdbc.update("UPDATE school_module_mapping_efox SET status=0,modified_date=GETDATE(),modified_by='SUPERADMIN' WHERE school_id=? AND module_id NOT IN (" + placeholders(ids.size()) + ")", args(schoolId, ids));
        jdbc.update("""
            INSERT INTO PageSchoolMapping_efox(PageId,School_id,PageDisplayName,PagePriority,Status,Created_Date,Created_By,ModuleId)
            SELECT p.PageId,?,p.Page_Name,p.Priority,1,GETDATE(),'SUPERADMIN',p.module_id
            FROM page_master_efox p JOIN school_module_mapping_efox sm ON sm.module_id=p.module_id AND sm.school_id=? AND sm.status=1
            WHERE p.status=1 AND p.IsDefault=1 AND NOT EXISTS
                (SELECT 1 FROM PageSchoolMapping_efox x WHERE x.PageId=p.PageId AND x.School_id=? AND x.Status=1)
            """, schoolId, schoolId, schoolId);
        jdbc.update("UPDATE PageSchoolMapping_efox SET Status=0,Modified_Date=GETDATE(),Modified_By='SUPERADMIN' WHERE School_id=? AND ModuleId NOT IN (SELECT module_id FROM school_module_mapping_efox WHERE school_id=? AND status=1)", schoolId, schoolId);
        jdbc.update("DELETE FROM Roles_Permission_efox WHERE school_id=? AND status=1", schoolId);
        jdbc.update("""
            INSERT INTO Roles_Permission_efox(Desi_id,pageid,module_id,created_by,Status,school_id)
            SELECT 28,PageId,ModuleId,'SUPERADMIN',1,School_id FROM PageSchoolMapping_efox WHERE school_id=? AND status=1
            """, schoolId);
        return Map.of("success", true, "message", "Modules assigned successfully", "selected", ids);
    }

    @Transactional
    public Map<String, Object> saveRoles(String schoolId, SuperAdminDtos.SelectionRequest request) {
        List<Integer> ids = cleanIds(request == null ? null : request.ids());
        for (Integer id : ids) {
            jdbc.update("""
                INSERT INTO TB_Desig(Desg_Id,Design_Name,Design_Code,Status,Created_By,Created_Date,school_id,SystemGenerated,DisplayName,Desig_Order)
                SELECT d.desig_id,d.desig_name,d.desig_code,'active','SUPERADMIN',GETDATE(),?,d.SystemGenerated,d.desig_name,CASE WHEN d.desig_id=28 THEN 1 ELSE 2 END
                FROM designation d WHERE d.desig_id=? AND NOT EXISTS
                    (SELECT 1 FROM TB_Desig x WHERE x.Desg_Id=? AND x.school_id=? AND x.status='active')
                """, schoolId, id, id, schoolId);
        }
        if (ids.isEmpty()) {
            jdbc.update("UPDATE TB_Desig SET status='inactive',Modified_Date=GETDATE(),Modified_By='SUPERADMIN' WHERE school_id=?", schoolId);
            jdbc.update("DELETE FROM desig_mapping WHERE school_id=?", schoolId);
        } else {
            jdbc.update("UPDATE TB_Desig SET status='inactive',Modified_Date=GETDATE(),Modified_By='SUPERADMIN' WHERE school_id=? AND Desg_Id NOT IN (" + placeholders(ids.size()) + ")", args(schoolId, ids));
            jdbc.update("DELETE FROM desig_mapping WHERE school_id=? AND Desg_Id NOT IN (" + placeholders(ids.size()) + ")", args(schoolId, ids));
        }
        return Map.of("success", true, "message", "Roles assigned successfully", "selected", ids);
    }

    private int count(String sql, Object... args) { Integer value = jdbc.queryForObject(sql, Integer.class, args); return value == null ? 0 : value; }

    private String generateSchoolId(String schoolName) {
        String base = java.util.Arrays.stream(schoolName.split(",", 2)[0].replace(".", "").trim().split("\\s+"))
            .filter(word -> !word.isBlank()).map(word -> word.substring(0, 1)).collect(Collectors.joining()).toUpperCase();
        if (base.isBlank()) base = "SCHOOL";
        for (int attempt = 0; attempt < 20; attempt++) {
            String candidate = base + (100 + RANDOM.nextInt(900));
            if (count("SELECT COUNT(*) FROM schoolmaster WHERE school_id=?", candidate) == 0) return candidate;
        }
        throw new IllegalStateException("Could not generate a unique school ID");
    }

    private static List<Integer> cleanIds(List<Integer> ids) {
        if (ids == null) return List.of();
        return ids.stream().filter(id -> id != null && id > 0).distinct().toList();
    }

    private static String placeholders(int count) { return String.join(",", java.util.Collections.nCopies(count, "?")); }

    private static Object[] args(String schoolId, List<Integer> ids) {
        List<Object> values = new ArrayList<>(); values.add(schoolId); values.addAll(ids); return values.toArray();
    }

    private static String text(String value) { return value == null ? "" : value.trim(); }
}
