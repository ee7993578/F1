package com.edufox.schoolerp.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MenuRepository {
    private final JdbcTemplate jdbc;

    public String findSchoolName(String schoolId) {
        List<String> values = jdbc.query("SELECT TOP (1) schoolname FROM schoolmaster WHERE school_id=? AND status=1",
            (rs, row) -> rs.getString(1), schoolId);
        return values.isEmpty() ? "" : values.get(0);
    }

    public String findCurrentSession(String schoolId) {
        List<String> values = jdbc.query("SELECT TOP (1) session FROM tb_session WHERE school_id=? AND status='Default'",
            (rs, row) -> rs.getString(1), schoolId);
        return values.isEmpty() ? "" : values.get(0);
    }

    public Map<String, Object> findSchoolProfile(String schoolId) {
        List<Map<String, Object>> values = jdbc.queryForList("""
            SELECT TOP (1)
                   COALESCE(tc_add1,'') AS address1, COALESCE(tc_add2,'') AS address2,
                   COALESCE(tc_add3,'') AS address3, COALESCE(tc_add4,'') AS address4,
                   COALESCE(phone,mobile,'') AS phone, COALESCE(logo_path,'') AS logo
            FROM schoolmaster WHERE school_id=? AND status=1
            """, schoolId);
        return values.isEmpty() ? Map.of() : values.get(0);
    }

    public List<ModuleRow> findStaffModules(String schoolId, String staffUid) {
        String sql = """
            SELECT DISTINCT smm.module_id, mm.module_name, mm.image_path,
                   COALESCE(smm.RedirectToOtherSoftware,'') AS redirect_url,
                   TRY_CONVERT(int, mm.priority) AS module_priority
            FROM school_module_mapping_efox smm
            JOIN module_master_efox mm ON mm.module_id=smm.module_id AND mm.status=1
            WHERE smm.school_id=? AND smm.status=1
              AND (
                EXISTS (
                  SELECT 1 FROM Roles_Permission_efox rp
                  WHERE rp.module_id=smm.module_id AND rp.school_id=? AND rp.status=1
                    AND (
                      EXISTS (SELECT 1 FROM desig_mapping dm
                              WHERE dm.Desg_id=rp.Desi_id AND dm.Staff_U_id=? AND dm.school_id=?)
                      OR EXISTS (SELECT 1 FROM TB_FacultyMaster f
                                 WHERE f.school_id=? AND f.staff_u_id=? AND rp.f_id=f.f_id)
                    )
                )
                OR NULLIF(LTRIM(RTRIM(COALESCE(smm.RedirectToOtherSoftware,''))),'') IS NOT NULL
              )
            ORDER BY TRY_CONVERT(int, mm.priority), smm.module_id
            """;
        return jdbc.query(sql, (rs, row) -> new ModuleRow(rs.getInt("module_id"), rs.getString("module_name"),
            rs.getString("image_path"), rs.getString("redirect_url"), intValue(rs, "module_priority")),
            schoolId, schoolId, staffUid, schoolId, schoolId, staffUid);
    }

    public Set<String> findAllowedPageIds(String schoolId, String staffUid) {
        String sql = """
            SELECT DISTINCT CAST(rp.pageid AS varchar(50)) AS page_id
            FROM Roles_Permission_efox rp
            WHERE rp.school_id=? AND rp.status=1
              AND (
                EXISTS (SELECT 1 FROM desig_mapping dm
                        WHERE dm.Desg_id=rp.Desi_id AND dm.Staff_U_id=? AND dm.school_id=?)
                OR EXISTS (SELECT 1 FROM TB_FacultyMaster f
                           WHERE f.school_id=? AND f.staff_u_id=? AND rp.f_id=f.f_id)
              )
            """;
        return new LinkedHashSet<>(jdbc.query(sql, (rs, row) -> rs.getString("page_id"),
            schoolId, staffUid, schoolId, schoolId, staffUid));
    }

    public List<PageRow> findSchoolPages(String schoolId) {
        String sql = """
            SELECT p.module_id, p.PageId, p.newpageid, p.Page_Name, p.Pageurl, p.icon,
                   p.parent_id, p.type, p.TypeDashboard,
                   COALESCE(NULLIF(em.PageDisplayName,''), p.Page_Name) AS efox_label,
                   COALESCE(em.PagePriority, TRY_CONVERT(int,p.Priority)) AS efox_priority,
                   COALESCE(NULLIF(rm.PageDisplayName,''), p.Page_Name) AS report_label,
                   COALESCE(rm.PagePriority, TRY_CONVERT(int,p.Priority)) AS report_priority,
                   mm.module_name
            FROM page_master_efox p
            JOIN module_master_efox mm ON mm.module_id=p.module_id AND mm.status=1
            JOIN school_module_mapping_efox smm ON smm.module_id=p.module_id
                 AND smm.school_id=? AND smm.status=1
            LEFT JOIN PageSchoolMapping_efox em ON em.PageId=p.newpageid
                 AND em.ModuleId=p.module_id AND em.School_id=? AND em.Status=1
            LEFT JOIN PageSchoolMapping rm ON rm.PageId=p.newpageid
                 AND rm.ModuleId=p.module_id AND rm.School_id=? AND rm.Status=1
            WHERE p.status=1
            ORDER BY p.module_id, TRY_CONVERT(int,p.Priority), p.PageId
            """;
        return jdbc.query(sql, (rs, row) -> new PageRow(rs.getInt("module_id"), rs.getInt("PageId"),
            rs.getInt("newpageid"), rs.getString("Page_Name"), rs.getString("Pageurl"), rs.getString("icon"),
            rs.getString("parent_id"), rs.getString("type"), rs.getString("TypeDashboard"),
            rs.getString("efox_label"), intValue(rs, "efox_priority"), rs.getString("report_label"),
            intValue(rs, "report_priority"), rs.getString("module_name")), schoolId, schoolId, schoolId);
    }

    public List<StudentPageRow> findStudentPages(String schoolId, String userType) {
        String sql = """
            SELECT page_id, page_name, page_order, page_url, module_id
            FROM student_panel_pages
            WHERE school_id=? AND status=1 AND (user_type=? OR ISNULL(user_type,'')='')
            ORDER BY page_order, id
            """;
        return jdbc.query(sql, (rs, row) -> new StudentPageRow(rs.getString("page_id"), rs.getString("page_name"),
            rs.getInt("page_order"), rs.getString("page_url"), rs.getString("module_id")), schoolId, userType);
    }

    private static int intValue(ResultSet rs, String column) {
        try { Integer value = (Integer) rs.getObject(column); return value == null ? 0 : value; }
        catch (Exception ignored) { return 0; }
    }

    public record ModuleRow(int moduleId, String moduleName, String icon, String redirectUrl, int priority) {}
    public record PageRow(int moduleId, int pageId, int newPageId, String pageName, String pageUrl, String icon,
        String parentId, String type, String typeDashboard, String efoxLabel, int efoxPriority,
        String reportLabel, int reportPriority, String moduleName) {
        public boolean isReport() { return "reports".equalsIgnoreCase(type); }
    }
    public record StudentPageRow(String pageId, String pageName, int priority, String pageUrl, String moduleId) {}
}
