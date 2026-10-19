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
        out.put("schools", schools(schoolId));
        out.put("moduleMappings", moduleMappings(schoolId, currentSession(schoolId)));
        out.put("pages", pages(schoolId));
        out.put("sessions", sessions(schoolId));
        out.put("terms", terms(schoolId, currentSession(schoolId)));
        out.put("termOptions", jdbc.queryForList("SELECT term_id AS id, term_name AS name FROM term ORDER BY term_id"));
        out.put("years", yearOptions());
        out.put("logs", logs(schoolId, null, null, null, null, null));
        out.put("profile", profile(schoolId, staffUid));
        out.put("profileOptions", profileOptions(schoolId));
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
            WHERE school_id=? AND LOWER(ISNULL(Status,'active'))='active'
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

    public List<Map<String, Object>> logs(String schoolId, String from, String to, String page, String activity, String staff) {
        StringBuilder sql = new StringBuilder("""
            SELECT TOP (500) logid AS id, activitytype AS activityType,
                   [Datetime] AS createdOn, full_pagename AS fullPageName,
                   ipaddress AS ipAddress, loginid AS loginId,
                   pagename AS pageName, loginid AS staffUid
            FROM useractivity_log
            WHERE school_id=?
            """);
        List<Object> args = new ArrayList<>();
        args.add(schoolId);
        if (from != null && !from.isBlank()) { sql.append(" AND CAST([Datetime] AS date)>=?"); args.add(from); }
        if (to != null && !to.isBlank()) { sql.append(" AND CAST([Datetime] AS date)<=?"); args.add(to); }
        if (page != null && !page.isBlank()) { sql.append(" AND pagename LIKE ?"); args.add("%" + page + "%"); }
        if (activity != null && !activity.isBlank()) { sql.append(" AND activitytype=?"); args.add(activity); }
        if (staff != null && !staff.isBlank()) { sql.append(" AND loginid=?"); args.add(staff); }
        sql.append(" ORDER BY [Datetime] DESC, logid DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public List<Map<String, Object>> schools(String actorSchoolId) {
        return jdbc.queryForList("""
            SELECT school_id AS id, schoolname AS name
            FROM schoolmaster
            WHERE ISNULL(status,1)=1
            ORDER BY schoolname, school_id
            """);
    }

    public List<Map<String, Object>> modulePageMappings(String schoolId, int moduleId) {
        return jdbc.queryForList("""
            SELECT p.PageId AS pageId, p.NewpageId AS newPageId, p.module_id AS moduleId,
                   p.Page_Name AS pageName,
                   COALESCE(ps.PageDisplayName,p.Page_Name) AS displayName,
                   COALESCE(ps.PagePriority,p.Priority) AS displayPriority,
                   CASE WHEN ps.id IS NULL OR ps.Status<>1 THEN CAST(0 AS bit)
                        ELSE CAST(1 AS bit) END AS checked
            FROM page_master_efox p
            LEFT JOIN PageSchoolMapping_efox ps
              ON ps.PageId=p.NewpageId AND ps.ModuleId=p.module_id AND ps.School_id=?
            WHERE p.module_id=? AND p.status=1
              AND p.PageId IN (SELECT DISTINCT NewpageId FROM page_master_efox WHERE NewpageId IS NOT NULL)
            ORDER BY p.Page_Name, p.PageId
            """, schoolId, moduleId);
    }

    @Transactional
    public void saveModulePageMappings(MasterSettingDtos.ModulePageMappingRequest request, String staffUid) {
        jdbc.update("UPDATE PageSchoolMapping_efox SET Status=0, Modified_Date=GETDATE(), Modified_By=? WHERE School_id=? AND ModuleId=?",
            staffUid, request.schoolId(), request.moduleId());
        for (MasterSettingDtos.ModulePageRow page : request.pages()) {
            if (!page.checked()) continue;
            String displayName = page.displayName() == null || page.displayName().isBlank()
                ? null : page.displayName().trim();
            int priority = page.priority() == null ? 0 : page.priority();
            int updated = jdbc.update("""
                UPDATE PageSchoolMapping_efox
                SET PageDisplayName=?, PagePriority=?, Status=1, Modified_Date=GETDATE(), Modified_By=?
                WHERE PageId=? AND School_id=? AND ModuleId=?
                """, displayName, priority, staffUid, page.pageId(), request.schoolId(), request.moduleId());
            if (updated == 0) {
                jdbc.update("""
                    INSERT INTO PageSchoolMapping_efox(PageId,School_id,PageDisplayName,PagePriority,Status,Created_Date,Created_By,ModuleId)
                    VALUES(?,?,?, ?,1,GETDATE(),?,?)
                    """, page.pageId(), request.schoolId(), displayName, priority, staffUid, request.moduleId());
            }
        }
    }

    public List<Map<String, Object>> staffRoles(String schoolId) {
        return jdbc.queryForList("""
            SELECT f.f_id AS facultyId, f.staff_u_id AS staffUid,
                   LTRIM(RTRIM(CONCAT(ISNULL(f.First_name,''),' ',ISNULL(f.Mid_name,''),' ',ISNULL(f.Last_name,'')))) AS faculty,
                   COALESCE(dep.department, CONVERT(varchar(100),f.Department),'') AS departmentName,
                   COALESCE(ds.DesignationName, d.Design_Name, '') AS designationName,
                   COALESCE(STRING_AGG(d2.Design_Name, ', '),'') AS roleName
            FROM TB_FacultyMaster f
            JOIN tb_faculty_withdrawn fw ON fw.f_id=f.f_id AND fw.school_id=f.school_id
                AND fw.status='Registered' AND fw.session=(SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC)
            LEFT JOIN department dep ON TRY_CONVERT(int,f.Department)=dep.row_id
            LEFT JOIN TB_Desig d ON d.Desg_Id=f.designation AND d.school_id=f.school_id
            LEFT JOIN Designation_Staff ds ON ds.DesignationId=f.designation
            LEFT JOIN desig_mapping dm ON dm.f_id=f.f_id AND dm.school_id=f.school_id
            LEFT JOIN TB_Desig d2 ON d2.Desg_Id=dm.Desg_id AND d2.school_id=dm.school_id
            WHERE f.school_id=?
            GROUP BY f.f_id,f.staff_u_id,f.First_name,f.Mid_name,f.Last_name,f.Department,dep.department,
                     ds.DesignationName,d.Design_Name
            ORDER BY faculty
            """, schoolId, schoolId);
    }

    public List<Integer> staffRoleIds(String schoolId, int facultyId) {
        return jdbc.query("SELECT Desg_id FROM desig_mapping WHERE school_id=? AND f_id=? ORDER BY Desg_id",
            (rs, row) -> rs.getInt(1), schoolId, facultyId);
    }

    @Transactional
    public void saveStaffRoles(String schoolId, String staffUid, MasterSettingDtos.StaffRoleSaveRequest request) {
        String uid = jdbc.queryForObject("SELECT CAST(staff_u_id AS varchar(100)) FROM TB_FacultyMaster WHERE school_id=? AND f_id=?",
            String.class, schoolId, request.facultyId());
        jdbc.update("DELETE FROM desig_mapping WHERE school_id=? AND f_id=? AND staff_u_id=?",
            schoolId, request.facultyId(), uid);
        for (Integer designationId : request.designationIds()) {
            jdbc.update("INSERT INTO desig_mapping(Desg_id,Staff_U_id,f_id,school_id) VALUES(?,?,?,?)",
                designationId, uid, request.facultyId(), schoolId);
        }
    }

    public List<Map<String, Object>> staffRolePages(String schoolId, int facultyId) {
        return jdbc.queryForList("""
            SELECT DISTINCT p.module_id AS moduleId, COALESCE(m.module_name,'Other') AS moduleName,
                   p.PageId AS pageId, p.Page_Name AS pageName, p.Pageurl AS pageUrl
            FROM roles_permission_efox r
            JOIN page_master_efox p ON p.PageId=r.pageid AND p.status=1
            LEFT JOIN module_master_efox m ON m.module_id=p.module_id
            WHERE r.school_id=? AND r.status=1 AND
              (r.f_id=? OR r.Desi_id IN (SELECT Desg_id FROM desig_mapping WHERE school_id=? AND f_id=?))
            ORDER BY moduleName, pageName
            """, schoolId, facultyId, schoolId, facultyId);
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
              f.salutation AS salutation, f.Joining_date AS joiningDate,
              f.Experience AS experience, f.Anniversary AS anniversaryDate,
              f.addharcard_no AS aadharCard, f.pancard_no AS panCard,
              f.acc_no AS accountNo, f.ifsc_code AS ifscCode,
              COALESCE(d.Design_Name,'') AS designationName
            FROM TB_FacultyMaster f
            LEFT JOIN TB_Desig d ON d.Desg_Id=f.designation AND d.school_id=f.school_id
            WHERE f.school_id=? AND CAST(f.staff_u_id AS varchar(50))=?
            """, schoolId, staffUid);
        if (values.isEmpty()) return Map.of();
        Map<String, Object> profile = new LinkedHashMap<>(values.get(0));
        Object facultyId = profile.get("facultyId");
        if (facultyId != null) {
            profile.put("subjects", jdbc.query("""
                SELECT sm.Subject AS name
                FROM TB_Faculty_Subject fs
                JOIN TB_SubjectMaster sm ON sm.id=fs.subject_id AND sm.school_id=fs.school_id
                WHERE fs.school_id=? AND fs.F_id=? AND sm.status=1
                ORDER BY sm.Subject
                """, (rs, row) -> rs.getString("name"), schoolId, facultyId));
        } else {
            profile.put("subjects", List.of());
        }
        return profile;
    }

    public Map<String, Object> profileOptions(String schoolId) {
        Map<String, Object> options = new LinkedHashMap<>();
        options.put("departments", jdbc.queryForList("""
            SELECT row_id AS id, department AS name FROM department
            WHERE school_id=? ORDER BY department
            """, schoolId));
        options.put("bloodGroups", jdbc.queryForList("SELECT id, blood_group AS name FROM blood_group ORDER BY id"));
        options.put("states", jdbc.queryForList("SELECT State_Id AS id, State_Name AS name FROM State ORDER BY State_Name"));
        options.put("districts", jdbc.queryForList("""
            SELECT City_Id AS id, City_Name AS name, state_id AS stateId
            FROM Districts ORDER BY City_Name
            """));
        options.put("subjects", jdbc.queryForList("""
            SELECT id, Subject AS name FROM TB_SubjectMaster
            WHERE school_id=? AND status=1 AND Subject IS NOT NULL ORDER BY Subject
            """, schoolId));
        return options;
    }

    public List<Map<String, Object>> permissionConfig(String schoolId, Integer designationId) {
        return jdbc.queryForList("""
            WITH mapped AS (
                SELECT ps.PageId AS pageId, ps.ModuleId AS moduleId,
                       ps.PageDisplayName AS mappedName, ps.PagePriority AS mappedPriority,
                       p.Page_Name AS pageName, p.Pageurl AS pageUrl, p.parent_id AS parentId,
                       ROW_NUMBER() OVER (PARTITION BY ps.PageId, ps.ModuleId ORDER BY p.PageId) AS rn
                FROM PageSchoolMapping_efox ps
                JOIN page_master_efox p ON p.NewpageId=ps.PageId AND p.module_id=ps.ModuleId
                WHERE ps.School_id=? AND ps.Status=1 AND p.status=1 AND ISNULL(p.type,'')=''
            )
            SELECT p.pageId AS pageId, p.pageId AS newPageId, p.moduleId AS moduleId,
                   COALESCE(p.mappedName,p.pageName) AS pageName, p.pageUrl AS pageUrl,
                   p.parentId AS parentId,
                   COALESCE(rp.ViewRole,0) AS viewRole, COALESCE(rp.AddRole,0) AS addRole,
                   COALESCE(rp.UpdateRole,0) AS updateRole, COALESCE(rp.DeleteRole,0) AS deleteRole,
                   COALESCE(rp.class_id,'') AS classIds,
                   CASE WHEN rp.Status=1 THEN CAST(1 AS bit) ELSE CAST(0 AS bit) END AS assigned
            FROM mapped p
            LEFT JOIN Roles_Permission_efox rp
              ON rp.pageid=p.pageId AND rp.module_id=p.moduleId
             AND rp.Desi_id=? AND rp.school_id=? AND rp.Status=1
            WHERE p.rn=1
            ORDER BY p.moduleId, COALESCE(p.mappedPriority,9999), p.pageId
            """, schoolId, designationId, schoolId);
    }

    public List<Map<String, Object>> staffPermissionConfig(String schoolId, int facultyId, int moduleId) {
        return jdbc.queryForList("""
            WITH mapped AS (
                SELECT ps.PageId AS pageId, ps.ModuleId AS moduleId,
                       ps.PageDisplayName AS mappedName, ps.PagePriority AS mappedPriority,
                       p.Page_Name AS pageName, p.Pageurl AS pageUrl, p.parent_id AS parentId,
                       ROW_NUMBER() OVER (PARTITION BY ps.PageId, ps.ModuleId ORDER BY p.PageId) AS rn
                FROM PageSchoolMapping_efox ps
                JOIN page_master_efox p ON p.NewpageId=ps.PageId AND p.module_id=ps.ModuleId
                WHERE ps.School_id=? AND ps.Status=1 AND p.status=1 AND p.module_id=? AND ISNULL(p.type,'')=''
            )
            SELECT p.pageId AS pageId, p.moduleId AS moduleId,
                   COALESCE(p.mappedName,p.pageName) AS pageName, p.pageUrl AS pageUrl,
                   p.parentId AS parentId,
                   CASE WHEN direct.Status=1 THEN CAST(1 AS bit) ELSE CAST(0 AS bit) END AS assigned,
                   CASE WHEN inherited.PageId IS NOT NULL THEN CAST(1 AS bit) ELSE CAST(0 AS bit) END AS inherited,
                   COALESCE(direct.class_id,'') AS classIds
            FROM mapped p
            LEFT JOIN Roles_Permission_efox direct
              ON direct.pageid=p.pageId AND direct.module_id=p.moduleId
             AND direct.f_id=? AND direct.school_id=? AND direct.Status=1
            LEFT JOIN (
                SELECT DISTINCT rp.pageid AS PageId
                FROM Roles_Permission_efox rp
                JOIN desig_mapping dm ON dm.Desg_id=rp.Desi_id AND dm.school_id=rp.school_id
                WHERE dm.f_id=? AND rp.school_id=? AND rp.Status=1
            ) inherited ON inherited.PageId=p.PageId
            WHERE p.rn=1
            ORDER BY COALESCE(p.mappedPriority,9999), p.pageId
            """, schoolId, moduleId, facultyId, schoolId, facultyId, schoolId);
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
    public void saveClass(String schoolId, String staffUid, MasterSettingDtos.ClassSaveRequest request) {
        if (request.classId() == null) {
            insertClass(schoolId, staffUid, request.className(), request.classGroup());
            return;
        }
        int updated = jdbc.update("""
            UPDATE class_name SET class_name=?, class_group=?, created_by=?
            WHERE id=? AND school_id=?
            """, request.className().trim().toUpperCase(Locale.ROOT), request.classGroup(),
            staffUid, request.classId(), schoolId);
        if (updated == 0) {
            jdbc.update("""
                INSERT INTO class_name(id,class_name,created_date,created_by,school_id,class_group)
                VALUES(?,?,GETDATE(),?,?,?)
                """, request.classId(), request.className().trim().toUpperCase(Locale.ROOT), staffUid,
                schoolId, request.classGroup());
        }
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

    public int updateSection(String schoolId, String staffUid, int sectionId, String sectionName, String sectionType) {
        return jdbc.update("""
            UPDATE TB_Class SET Section=?, ClassType=COALESCE(?,ClassType),
                   modified_date=GETDATE(), modified_by=?
            WHERE Sec_id=? AND school_id=?
            """, sectionName.trim().toUpperCase(Locale.ROOT), sectionType, staffUid,
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
            VALUES(?,?,?,'active',?,CAST(GETDATE() AS date),?,0)
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
        for (MasterSettingDtos.PermissionRow p : request.permissions()) {
            if (!p.view() && !p.add() && !p.update() && !p.delete()) continue;
            String classIds = p.classIds() == null ? "" : p.classIds();
            int updated = jdbc.update("""
                UPDATE Roles_Permission_efox SET Status=1,class_id=?,created_by=?
                WHERE Desi_id=? AND pageid=? AND module_id=? AND school_id=?
                """, classIds, staffUid, request.designationId(), p.pageId(), p.moduleId(), schoolId);
            if (updated == 0) {
                jdbc.update("""
                    INSERT INTO Roles_Permission_efox(Desi_id,pageid,module_id,Status,created_by,school_id,class_id)
                    VALUES(?,?,?,1,?,?,?)
                    """, request.designationId(), p.pageId(), p.moduleId(), staffUid, schoolId, classIds);
            }
        }
    }

    @Transactional
    public void saveStaffPermissions(String schoolId, String staffUid,
                                     MasterSettingDtos.StaffPermissionSaveRequest request) {
        int facultyId = request.facultyId();
        int moduleId = request.moduleId();
        String classIds = request.classIds() == null ? "" : request.classIds();
        jdbc.update("""
            UPDATE Roles_Permission_efox SET Status=0,class_id=''
            WHERE f_id=? AND school_id=? AND module_id=?
            """, facultyId, schoolId, moduleId);
        for (Integer pageId : request.pageIds()) {
            Integer inherited = jdbc.queryForObject("""
                SELECT COUNT(*) FROM Roles_Permission_efox rp
                JOIN desig_mapping dm ON dm.Desg_id=rp.Desi_id AND dm.school_id=rp.school_id
                WHERE dm.f_id=? AND rp.pageid=? AND rp.module_id=? AND rp.school_id=? AND rp.Status=1
                """, Integer.class, facultyId, pageId, moduleId, schoolId);
            if (inherited != null && inherited > 0) continue;
            int updated = jdbc.update("""
                UPDATE Roles_Permission_efox SET Status=1,class_id='',created_by=?
                WHERE f_id=? AND pageid=? AND module_id=? AND school_id=?
                """, staffUid, facultyId, pageId, moduleId, schoolId);
            if (updated == 0) {
                jdbc.update("""
                    INSERT INTO Roles_Permission_efox(Desi_id,pageid,module_id,Status,created_by,school_id,class_id,f_id)
                    VALUES(0,?,?,1,?,?,?,?)
                    """, pageId, moduleId, staffUid, schoolId, classIds, facultyId);
            }
        }
    }

    public List<Map<String, Object>> mobilePermissions(String schoolId, int designationId) {
        return jdbc.queryForList("""
            SELECT p.id AS pageId, p.PageName AS pageName, p.PageUrl AS pageUrl,
                   CASE WHEN ISNULL(em.EntityId,0)=101 THEN 'Management' ELSE 'Faculty' END AS userType,
                   CASE WHEN a.PageStatus=1 THEN CAST(1 AS bit) ELSE CAST(0 AS bit) END AS assigned
            FROM New_MobileApp_MenuPages p
            JOIN New_MobileApp_MenuPagesSchoolMapping sm
              ON sm.PageId=p.id AND sm.School_Id=? AND sm.PageStatus=1
            LEFT JOIN New_MobileApp_AssignPages a
              ON a.PageId=p.id AND a.School_Id=? AND a.Desig_id=? AND a.MenuType='SideMenu' AND a.PageStatus=1
            LEFT JOIN Model_EntityDesignationMapping em
              ON em.Desg_Id=? AND em.school_id=? AND ISNULL(em.IsDeleted,0)=0
            WHERE p.PageStatus=1
            ORDER BY p.id
            """, schoolId, schoolId, designationId, designationId, schoolId);
    }

    @Transactional
    public void saveMobilePermissions(String schoolId, String staffUid,
                                      MasterSettingDtos.MobilePermissionSaveRequest request) {
        String userType = jdbc.queryForObject("""
            SELECT CASE WHEN ISNULL(MAX(EntityId),0)=101 THEN 'Management' ELSE 'Faculty' END
            FROM Model_EntityDesignationMapping WHERE school_id=? AND Desg_Id=? AND ISNULL(IsDeleted,0)=0
            """, String.class, schoolId, request.designationId());
        jdbc.update("UPDATE New_MobileApp_AssignPages SET PageStatus=0 WHERE School_Id=? AND Desig_id=? AND MenuType=?",
            schoolId, request.designationId(), request.menuType());
        int order = 1;
        for (Integer pageId : request.pageIds()) {
            jdbc.update("""
                INSERT INTO New_MobileApp_AssignPages(PageId,PageOrder,PageStatus,MenuType,User_Type,School_Id,Desig_id)
                VALUES(?,?,1,?,?,?,?)
                """, pageId, order++, request.menuType(), userType, schoolId, request.designationId());
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
            Map.entry("fatherName","FH_Name"), Map.entry("dob","DOB"),
            Map.entry("maritalStatus","M_status"), Map.entry("residentialAddress","R_Address"),
            Map.entry("presentAddress","Present_Address"), Map.entry("pin","Pin"),
            Map.entry("city","City"), Map.entry("department","Department"),
            Map.entry("stateId","State_id"), Map.entry("districtId","District_id"),
            Map.entry("bloodGroup","Blood_gp"), Map.entry("bQualification","BQualification"),
            Map.entry("hQualification","HQualification"), Map.entry("joiningDate","Joining_date"),
            Map.entry("experience","Experience"), Map.entry("anniversaryDate","Anniversary"),
            Map.entry("aadharCard","addharcard_no"), Map.entry("panCard","pancard_no"),
            Map.entry("accountNo","acc_no"), Map.entry("ifscCode","ifsc_code"),
            Map.entry("imagePath","Image_path")
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

    /**
     * Java/JDBC equivalent of the legacy sw_session_* actions.  The legacy
     * database remains the source of truth, but no stored procedure is called.
     */
    @Transactional
    public int copySessionConfiguration(String schoolId, String staffUid, String from, String to, String action) {
        return switch (action) {
            case "faculty" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO tb_faculty_withdrawn(f_id,status,w_date,Remark,created_date,created_by,modified_date,modified_by,school_id,session,staff_u_id)
                SELECT f_id,status,w_date,Remark,GETDATE(),?,GETDATE(),?,?,staff_u_id
                FROM tb_faculty_withdrawn WHERE school_id=? AND session=? AND status='Registered'
                """, staffUid, staffUid, schoolId, to, schoolId, from);
            case "classsub" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO TB_ClassSubject(class_id,subject_id,subject_code,isoptional,isgrade,isprac,subject_order,status,created_date,created_by,modified_date,modified_by,school_id,session)
                SELECT class_id,subject_id,subject_code,isoptional,isgrade,isprac,subject_order,status,GETDATE(),?,GETDATE(),?,?,?
                FROM TB_ClassSubject WHERE school_id=? AND session=?
                """, staffUid, staffUid, schoolId, to, schoolId, from);
            case "prac" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO tb_pract_subject(subject_id,Pr_subject_id,sec_id,created_date,created_by,session,school_id)
                SELECT subject_id,Pr_subject_id,sec_id,created_date,?,?,?
                FROM tb_pract_subject WHERE school_id=? AND session=?
                """, staffUid, to, schoolId, schoolId, from);
            case "weightage" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO tb_weightagemarks_new(class_id,exam_id,exam_type_id,term,weightage,created_date,created_by,modified_date,modified_by,school_id,session)
                SELECT class_id,exam_id,exam_type_id,term,weightage,GETDATE(),?,GETDATE(),?,?,?
                FROM tb_weightagemarks_new WHERE school_id=? AND session=?
                """, staffUid, staffUid, schoolId, to, schoolId, from);
            case "header" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO header_name(header_id,header_name,session,created_date,created_by,modified_date,modified_by,status,school_id,header_order)
                SELECT header_id,header_name,?,GETDATE(),?,GETDATE(),?,status,school_id,header_order
                FROM header_name WHERE school_id=? AND session=?
                """, to, staffUid, staffUid, schoolId, from);
            case "category" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO category_name(category_id,header_id,category_name,session,class_id,created_date,created_by,modified_date,modified_by,status,school_id,cat_order)
                SELECT category_id,header_id,category_name,?,class_id,GETDATE(),?,GETDATE(),?,status,school_id,cat_order
                FROM category_name WHERE school_id=? AND session=?
                """, to, staffUid, staffUid, schoolId, from);
            case "attribute" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO attribute_name(attribute_id,header_id,category_id,attribute_name,attribute_order,session,class_id,created_date,created_by,modified_date,modified_by,status,school_id)
                SELECT attribute_id,header_id,category_id,attribute_name,attribute_order,?,class_id,GETDATE(),?,GETDATE(),?,status,school_id
                FROM attribute_name WHERE school_id=? AND session=?
                """, to, staffUid, staffUid, schoolId, from);
            case "indicator" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO tb_indicator(indicator_ID,school_id,session,indicator_cat,indicator_Name,IndicatorOrder,status,created_by,created_date,modified_by,modified_date,class_id,indicator_category_id)
                SELECT indicator_ID,school_id,?,indicator_cat,indicator_Name,IndicatorOrder,status,?,GETDATE(),?,GETDATE(),class_id,indicator_category_id
                FROM tb_indicator WHERE school_id=? AND session=?
                """, to, staffUid, staffUid, schoolId, from);
            case "clsteacher" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO tb_classteacher(section_id,class_id,staff_u_id,f_id,created_date,created_by,modified_date,modified_by,school_id,session,f_id_co)
                SELECT section_id,class_id,staff_u_id,f_id,GETDATE(),?,modified_date,modified_by,?,?,f_id_co
                FROM tb_classteacher WHERE school_id=? AND session=?
                """, staffUid, schoolId, to, schoolId, from);
            case "subsubject" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO subsubject(class_id,subject_id,sub_subject_id,created_date,created_by,session,school_id)
                SELECT class_id,subject_id,sub_subject_id,GETDATE(),?,?,?
                FROM subsubject WHERE school_id=? AND session=?
                """, staffUid, to, schoolId, schoolId, from);
            case "feehead" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO fee_head_master(fee_head_id,fee_type_id,fee_head,fee_code,[order],status,created_date,created_by,modified_date,modified_by,school_id,session,isOptional,sub_id,isRefundable,is_security,display_fee_head,FEEHEADFOR,common_head_id,Opt_head_type,parentid,Account_id)
                SELECT fee_head_id,fee_type_id,fee_head,fee_code,[order],status,GETDATE(),?,'','',?,?,isOptional,sub_id,isRefundable,is_security,display_fee_head,FEEHEADFOR,common_head_id,Opt_head_type,parentid,Account_id
                FROM fee_head_master WHERE school_id=? AND session=? AND fee_type_id<>6
                """, staffUid, schoolId, to, schoolId, from);
            case "transport" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO fee_head_master(fee_head_id,fee_type_id,fee_head,fee_code,[order],status,created_date,created_by,modified_by,school_id,session,isOptional,sub_id,isRefundable,is_security,display_fee_head,FEEHEADFOR,common_head_id,Opt_head_type,parentid,Account_id)
                SELECT fee_head_id,fee_type_id,fee_head,fee_code,[order],status,GETDATE(),?,'',?,?,isOptional,sub_id,isRefundable,is_security,display_fee_head,FEEHEADFOR,common_head_id,Opt_head_type,parentid,Account_id
                FROM fee_head_master WHERE school_id=? AND session=? AND fee_type_id=6
                """, staffUid, schoolId, to, schoolId, from);
            case "feecharge" -> feeCharge(schoolId, staffUid, from, to, false);
            case "transportcharges" -> feeCharge(schoolId, staffUid, from, to, true);
            case "installment" -> installment(schoolId, staffUid, from, to, false);
            case "transportinst" -> installment(schoolId, staffUid, from, to, true);
            case "latefee" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO late_fee(inst_no,from_date,to_date,mode,charge,status,created_date,created_by,modified_by,school_id,session,fee_type_id)
                SELECT inst_no,DATEADD(year,1,from_date),DATEADD(year,1,to_date),mode,charge,status,GETDATE(),?,'',school_id,?,fee_type_id
                FROM late_fee WHERE school_id=? AND session=?
                """, staffUid, to, schoolId, from);
            case "miscell" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO fee_miscellaneous_head(miscellaneous_type_id,miscellaneous_type,school_id,session,created_by,created_date)
                SELECT miscellaneous_type_id,miscellaneous_type,?,?,GETDATE()
                FROM fee_miscellaneous_head WHERE school_id=? AND session=?
                """, schoolId, to, staffUid, schoolId, from);
            case "maxmin" -> copyMaxMin(schoolId, staffUid, from, to);
            case "maxminprimary" -> copyMaxMinPrimary(schoolId, staffUid, from, to);
            case "facultysubmap" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO faculty_subject_mapping(f_id,staff_u_id,sec_id,sub_id,school_id,session,created_by,created_date)
                SELECT f_id,staff_u_id,sec_id,sub_id,school_id,?, ?,GETDATE() FROM faculty_subject_mapping t1
                WHERE school_id=? AND session=? AND CONCAT(t1.f_id,t1.sec_id,t1.sub_id) NOT IN
                  (SELECT CONCAT(t2.f_id,t2.sec_id,t2.sub_id) FROM faculty_subject_mapping t2 WHERE school_id=? AND session=?)
                """, to, staffUid, schoolId, from, schoolId, to);
            case "concess" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO concession_group_name(group_code,Group_name,school_id,created_by,created_date,session)
                SELECT group_code,Group_name,school_id,?,GETDATE(),? FROM concession_group_name WHERE school_id=? AND session=?
                """, staffUid, to, schoolId, from);
            case "concessval" -> copy(schoolId, staffUid, from, to, """
                INSERT INTO concession_group_details(group_code,con_type,installment_no,fee_type_id,fee_head_id,con_value,school_id,created_date,created_by,session)
                SELECT group_code,con_type,installment_no,fee_type_id,fee_head_id,con_value,school_id,GETDATE(),?,? FROM concession_group_details WHERE school_id=? AND session=?
                """, staffUid, to, schoolId, from);
            case "timetable" -> copyTimetable(schoolId, staffUid, from, to);
            default -> throw new IllegalArgumentException("Unknown session action: " + action);
        };
    }

    public Map<String, Boolean> sessionSwitchStatus(String schoolId, String session) {
        Map<String, Boolean> result = new LinkedHashMap<>();
        result.put("faculty", hasRows("tb_faculty_withdrawn", schoolId, session, ""));
        result.put("classsub", hasRows("TB_ClassSubject", schoolId, session, ""));
        result.put("prac", hasRows("tb_pract_subject", schoolId, session, ""));
        result.put("weightage", hasRows("tb_weightagemarks_new", schoolId, session, ""));
        result.put("maxmin", hasRows("tb_exam_max_min_new", schoolId, session, ""));
        result.put("header", hasRows("header_name", schoolId, session, ""));
        result.put("category", hasRows("category_name", schoolId, session, ""));
        result.put("attribute", hasRows("attribute_name", schoolId, session, ""));
        result.put("indicator", hasRows("tb_indicator", schoolId, session, ""));
        result.put("clsteacher", hasRows("tb_classteacher", schoolId, session, ""));
        result.put("subsubject", hasRows("subsubject", schoolId, session, ""));
        result.put("timetable", hasRows("TB_Timetables_config", schoolId, session, ""));
        result.put("feehead", hasRows("fee_head_master", schoolId, session, "fee_type_id<>6"));
        result.put("feecharge", hasRows("fee_head_charge", schoolId, session, "fee_type_id<>6"));
        result.put("installment", hasRows("Installment_Master", schoolId, session, "fee_type_id<>6"));
        result.put("latefee", hasRows("late_fee", schoolId, session, ""));
        result.put("miscell", hasRows("fee_miscellaneous_head", schoolId, session, ""));
        result.put("transport", hasRows("fee_head_master", schoolId, session, "fee_type_id=6"));
        result.put("transportcharges", hasRows("fee_head_charge", schoolId, session, "fee_type_id=6"));
        result.put("transportinst", hasRows("Installment_Master", schoolId, session, "fee_type_id=6"));
        result.put("maxminprimary", hasRows("tb_exam_max_min_primary", schoolId, session, ""));
        result.put("facultysubmap", hasRows("faculty_subject_mapping", schoolId, session, ""));
        result.put("concess", hasRows("concession_group_name", schoolId, session, ""));
        result.put("concessval", hasRows("concession_group_details", schoolId, session, ""));
        return result;
    }

    private boolean hasRows(String table, String schoolId, String session, String extraWhere) {
        String safeTable = table.replaceAll("[^A-Za-z0-9_]", "");
        String sql = "SELECT TOP 1 1 FROM " + safeTable + " WHERE school_id=? AND session=?";
        if (extraWhere != null && !extraWhere.isBlank()) sql += " AND " + extraWhere;
        return !jdbc.queryForList(sql, schoolId, session).isEmpty();
    }

    private int copy(String schoolId, String staffUid, String from, String to, String sql, Object... args) {
        return jdbc.update(sql, args);
    }

    private int feeCharge(String schoolId, String staffUid, String from, String to, boolean transport) {
        return copy(schoolId, staffUid, from, to, """
            INSERT INTO fee_head_charge(fee_head_id,fee_type_id,fee_charges,class_id,status,created_date,created_by,modified_by,school_id,session,school_type,transport_type_id)
            SELECT fee_head_id,fee_type_id,fee_charges,class_id,status,GETDATE(),?,'',?,?,school_type,transport_type_id
            FROM fee_head_charge WHERE school_id=? AND session=? AND fee_type_id %s
            """.formatted(transport ? "=6" : "<>6"), staffUid, schoolId, to, schoolId, from);
    }

    private int installment(String schoolId, String staffUid, String from, String to, boolean transport) {
        return copy(schoolId, staffUid, from, to, """
            INSERT INTO Installment_Master(installment_no,fee_type_id,fee_head_id,month_id,due_date,status,created_date,created_by,modified_by,school_id,session,installment_dis,inst_code,other_installment_dis)
            SELECT installment_no,fee_type_id,fee_head_id,month_id,DATEADD(year,1,due_date),status,GETDATE(),?,'',?,?,installment_dis,inst_code,other_installment_dis
            FROM Installment_Master WHERE school_id=? AND session=? AND fee_type_id %s
            """.formatted(transport ? "=6" : "<>6"), staffUid, schoolId, to, schoolId, from);
    }

    private int copyMaxMin(String schoolId, String staffUid, String from, String to) {
        return jdbc.update("""
            INSERT INTO tb_exam_max_min_new(id,class_id,sec_id,subject_id,min_marks,max_marks,Grade_subject,E_Date,Exam_id,Exam_type_id,Term_id,Optional,Grade_Marks,created_date,created_by,modified_date,modified_by,school_id,Session)
            SELECT base.next_id + ROW_NUMBER() OVER(ORDER BY id),class_id,sec_id,subject_id,min_marks,max_marks,Grade_subject,E_Date,Exam_id,Exam_type_id,Term_id,Optional,Grade_Marks,GETDATE(),?,GETDATE(),?,school_id,?
            FROM tb_exam_max_min_new CROSS JOIN (SELECT ISNULL(MAX(id),0) AS next_id FROM tb_exam_max_min_new WHERE school_id=?) base
            WHERE school_id=? AND Session=?
            """, staffUid, staffUid, to, schoolId, schoolId, from);
    }

    private int copyMaxMinPrimary(String schoolId, String staffUid, String from, String to) {
        return jdbc.update("""
            INSERT INTO tb_exam_max_min_primary(id,class_id,header_id,category_id,attribute_id,min_marks,max_marks,grade_subject,exam_date,exam_type_id,term,created_date,created_by,modified_date,modified_by,school_id,session)
            SELECT base.next_id + ROW_NUMBER() OVER(ORDER BY id),class_id,header_id,category_id,attribute_id,min_marks,max_marks,grade_subject,exam_date,exam_type_id,term,CONVERT(varchar,GETDATE(),23),?,CONVERT(varchar,GETDATE(),23),?,school_id,?
            FROM tb_exam_max_min_primary CROSS JOIN (SELECT ISNULL(MAX(id),0) AS next_id FROM tb_exam_max_min_primary WHERE school_id=?) base
            WHERE school_id=? AND session=?
            """, staffUid, staffUid, to, schoolId, schoolId, from);
    }

    private int copyTimetable(String schoolId, String staffUid, String from, String to) {
        int rows = 0;
        List<String> uids = jdbc.query("SELECT DISTINCT TT_UID FROM TB_Timetables_config WHERE school_id=? AND session=?",
            (rs, row) -> rs.getString(1), schoolId, from);
        for (String sourceUid : uids) {
            String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
            rows += jdbc.update("""
                INSERT INTO TB_Timetables_config(TT_ID,TT_Config,lecture_id,lecture_name,TT_UID,TT_Duration_Nm,ST_Time_Hr,ST_Time_Min,ST_Time_Mode,Duration_Time_Min,END_Time_Hr,END_Time_Min,END_Time_Mode,Break_Time,Status,Created_by,Created_Date,school_id,session)
                SELECT TT_ID,TT_Config,lecture_id,lecture_name,?,TT_Duration_Nm,ST_Time_Hr,ST_Time_Min,ST_Time_Mode,Duration_Time_Min,END_Time_Hr,END_Time_Min,END_Time_Mode,Break_Time,Status,?,GETDATE(),?,?
                FROM TB_Timetables_config WHERE school_id=? AND session=? AND TT_UID=?
                """, uid, staffUid, schoolId, to, schoolId, from, sourceUid);
            rows += jdbc.update("""
                INSERT INTO TB_Period_Time(Period,start_time,End_time,tt_uid,school_id,session)
                SELECT Period,start_time,End_time,?,?,? FROM TB_Period_Time WHERE school_id=? AND session=? AND TT_UID=?
                """, uid, schoolId, to, schoolId, from, sourceUid);
            rows += jdbc.update("""
                INSERT INTO TB_TimeTable(F_id,TDay,Period,sec_id,Subject_id,TT_ID,TT_UID,created_date,created_by,modified_date,modified_by,school_id,session)
                SELECT tt.F_id,tt.TDay,tt.Period,tt.sec_id,tt.Subject_id,tt.TT_ID,?,GETDATE(),?,tt.modified_date,tt.modified_by,?,?
                FROM TB_TimeTable tt WHERE tt.school_id=? AND tt.session=? AND tt.TT_UID=?
                  AND NOT EXISTS (SELECT 1 FROM tb_faculty_withdrawn fw WHERE fw.status<>'Registered' AND fw.f_id=tt.f_id AND fw.session=? AND fw.school_id=tt.school_id)
                """, uid, staffUid, schoolId, to, schoolId, from, sourceUid, to);
        }
        return rows;
    }

    private static int yes(boolean value) { return value ? 1 : 0; }
    private static Integer parseInt(String value) {
        try { return Integer.valueOf(value); } catch (Exception ignored) { return null; }
    }
}
