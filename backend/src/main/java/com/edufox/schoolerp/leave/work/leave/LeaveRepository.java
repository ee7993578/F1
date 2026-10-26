package com.edufox.schoolerp.leave.work.leave;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class LeaveRepository {
    private final JdbcTemplate jdbc;

    private static String s(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static Map<String, Object> row(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i + 1 < values.length; i += 2) result.put(String.valueOf(values[i]), values[i + 1]);
        return result;
    }

    public List<Map<String, Object>> sessions(String school) {
        return jdbc.queryForList("SELECT session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN LOWER(status)='default' THEN 0 ELSE 1 END,session DESC", school);
    }

    public String currentSession(String school) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND LOWER(status)='default'", school);
        return rows.isEmpty() ? "" : s(rows.get(0).get("session"));
    }

    public Map<String, Object> bootstrap(String school, String staff) {
        String session = currentSession(school);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("session", session);
        result.put("sessions", sessions(school));
        result.put("leaveTypes", jdbc.queryForList("SELECT leaveTypeid AS leaveTypeId,leaveType,leavetypeDisplay AS leaveTypeDisplay FROM leaveType WHERE school_Id=? AND status=1 ORDER BY leaveType", school));
        result.put("faculty", jdbc.queryForList("SELECT DISTINCT f.staff_u_id AS staffUid,UPPER(LTRIM(RTRIM(COALESCE(f.first_name,'')+' '+COALESCE(f.mid_name,'')+' '+COALESCE(f.last_name,'')))) AS name,f.Department AS department,f.designation FROM TB_FacultyMaster f JOIN tb_faculty_withdrawn fw ON fw.school_id=f.school_id AND fw.staff_u_id=f.staff_u_id WHERE f.school_id=? AND fw.status='Registered' AND fw.session=? ORDER BY name", school, session));
        result.put("wingIncharges", wingIncharges(school, session));
        result.put("currentStaff", jdbc.queryForList("SELECT TOP 1 staff_u_id AS staffUid,UPPER(LTRIM(RTRIM(COALESCE(salutation,'')+' '+COALESCE(first_name,'')+' '+COALESCE(mid_name,'')+' '+COALESCE(last_name,'')))) AS name,Department AS department,designation FROM TB_FacultyMaster WHERE school_id=? AND staff_u_id=?", school, staff));
        result.put("isAdmin", jdbc.queryForObject("SELECT CASE WHEN EXISTS(SELECT 1 FROM desig_mapping WHERE school_id=? AND Staff_U_id=? AND Desg_id=28) THEN 1 ELSE 0 END", Integer.class, school, staff));
        return result;
    }

    public List<Map<String, Object>> wingIncharges(String school, String session) {
        return jdbc.queryForList("SELECT DISTINCT w.staff_u_id AS staffUid,CASE WHEN w.desig_id=64 THEN COALESCE(d.Design_Name,'') ELSE COALESCE(f.salutation,'')+' '+UPPER(COALESCE(f.first_name,'')+' '+COALESCE(f.mid_name,'')+' '+COALESCE(f.last_name,''))+' [ '+COALESCE(d.Design_Name,'')+' ]' END AS name,w.desig_id AS designationId,w.[order] AS displayOrder FROM wingincharge w LEFT JOIN TB_FacultyMaster f ON f.school_id=w.school_id AND f.staff_u_id=TRY_CONVERT(int,w.staff_u_id) LEFT JOIN tb_desig d ON d.school_id=w.school_id AND d.Desg_Id=TRY_CONVERT(int,w.desig_id) WHERE w.school_id=? AND w.session=? AND w.status=1 ORDER BY displayOrder,name", school, session);
    }

    public List<Map<String, Object>> leaveTypes(String school) {
        return jdbc.queryForList("SELECT leaveTypeid AS leaveTypeId,leaveType,leavetypeDisplay AS leaveTypeDisplay FROM leaveType WHERE school_Id=? AND status=1 ORDER BY leaveType", school);
    }

    public Map<String, Object> saveLeaveType(String school, String type, String display) {
        Integer duplicate = jdbc.queryForObject("SELECT COUNT(1) FROM leaveType WHERE school_Id=? AND status=1 AND (leaveType=? OR leavetypeDisplay=?)", Integer.class, school, type, display);
        if (duplicate != null && duplicate > 0) throw new IllegalArgumentException("Leave Type Or Leave type display already defined");
        Integer next = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int,leaveTypeid)),0)+1 FROM leaveType WHERE school_Id=?", Integer.class, school);
        jdbc.update("INSERT INTO leaveType(leaveTypeid,leaveType,school_Id,leavetypeDisplay,status) VALUES(?,?,?,?,1)", next == null ? 1 : next, type, school, display);
        return row("message", "Leave Define Successfully");
    }

    public List<Map<String, Object>> facultyLeaveDetails(String school, String staff, String session) {
        return jdbc.queryForList("SELECT l.leaveTypeid AS leaveTypeId,l.leaveType AS leaveTypeDisplay,(SELECT TOP 1 ft.leavedue FROM facultytotalleave ft WHERE ft.school_id=? AND ft.staff_u_id=? AND ft.session=? AND ft.leaveTypeid=l.leaveTypeid AND ft.status=1) AS leaveDue,ISNULL((SELECT SUM(TRY_CONVERT(int,lr.leaveNowAppliedFor)) FROM facultyleaverecord lr JOIN faculty_leaveapplicationform fl ON fl.ApplicationId=lr.ApplicationId AND fl.school_id=lr.school_id WHERE lr.school_id=? AND fl.staff_u_id=? AND fl.session=? AND fl.status=1 AND lr.leaveType=l.leaveTypeid),0) AS leaveNowAppliedFor,ISNULL((SELECT SUM(TRY_CONVERT(int,lr.leaveNowAppliedFor)) FROM facultyleaverecord lr JOIN faculty_leaveapplicationform fl ON fl.ApplicationId=lr.ApplicationId AND fl.school_id=lr.school_id WHERE lr.school_id=? AND fl.staff_u_id=? AND fl.session=? AND fl.principal_approval='Approved' AND lr.leaveType=l.leaveTypeid),0) AS leaveApproved FROM leaveType l WHERE l.school_Id=? AND l.status=1 ORDER BY l.leaveType", school, staff, session, school, staff, session, school, staff, session, school);
    }

    public void saveAllocations(String school, String staff, String session, String createdBy, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) throw new IllegalArgumentException("No leave allocation rows");
        for (Map<String, Object> item : rows) {
            String type = s(item.get("leaveTypeId"));
            if (type.isBlank()) continue;
            int due = Integer.parseInt(s(item.get("leaveDue")).isBlank() ? "0" : s(item.get("leaveDue")));
            jdbc.update("UPDATE facultytotalleave SET status=0 WHERE school_id=? AND leaveTypeid=? AND staff_u_id=? AND session=?", school, type, staff, session);
            jdbc.update("INSERT INTO facultytotalleave(leaveTypeid,staff_u_id,leavedue,created_by,school_id,status,session) VALUES(?,?,?,?,?,1,?)", type, staff, due, createdBy, school, session);
        }
    }

    public List<Map<String, Object>> applicationsForHead(String school, String session, String head, String status, boolean admin) {
        String sql = "SELECT l.ApplicationId AS applicationId,l.Name AS name,l.NatureofLeaveapplied AS natureOfLeave,l.DatesFrom AS datesFrom,l.DatesTo AS datesTo,CASE WHEN l.headofdepartment_approval IS NULL THEN 'Approve' WHEN l.headofdepartment_approval='Approved' THEN 'Not Approve' ELSE 'Approve' END AS actionLabel,COALESCE(l.headofdepartment_approval,'') AS headStatus,l.created_date AS createdDate,COALESCE(l.principal_approval,'') AS principalStatus,l.headofdepartment AS headUid,COALESCE((SELECT TOP 1 remark FROM faculty_leaveApplicationStatus ls WHERE ls.ApplicationId=l.ApplicationId AND ls.school_id=l.school_id AND ls.staff_u_id=? ORDER BY ls.id DESC),'') AS remark,l.[Date] AS leaveApplyOn,l.Reason AS reason FROM faculty_leaveapplicationform l WHERE l.school_id=? AND l.status=1 AND l.session=?";
        List<Object> params = new ArrayList<>(List.of(head, school, session));
        if (!admin) { sql += " AND l.headofdepartment=?"; params.add(head); }
        if (!s(status).isBlank() && !"0".equals(status)) { sql += " AND COALESCE(l.headofdepartment_approval,'')=?"; params.add(status); }
        return attachHeadNames(jdbc.queryForList(sql + " ORDER BY l.created_date DESC", params.toArray()), school, session);
    }

    public List<Map<String, Object>> applicationsForPrincipal(String school, String session, String head, String status) {
        String sql = "SELECT l.ApplicationId AS applicationId,UPPER(l.Name) AS name,l.NatureofLeaveapplied AS natureOfLeave,l.DatesFrom AS datesFrom,l.DatesTo AS datesTo,CASE WHEN l.principal_approval IS NULL THEN 'Approve' WHEN l.principal_approval='Approved' THEN 'Not Approve' ELSE 'Approve' END AS actionLabel,COALESCE(l.principal_approval,'') AS principalStatus,l.created_date AS createdDate,COALESCE(l.headofdepartment_approval,'') AS headStatus,l.headofdepartment AS headUid,COALESCE((SELECT TOP 1 remark FROM faculty_leaveApplicationStatus ls WHERE ls.ApplicationId=l.ApplicationId AND ls.school_id=l.school_id AND ls.staff_u_id=? ORDER BY ls.id DESC),'') AS remark,l.[Date] AS leaveApplyOn,l.Reason AS reason FROM faculty_leaveapplicationform l WHERE l.school_id=? AND l.status=1 AND l.session=?";
        List<Object> params = new ArrayList<>(List.of(head, school, session));
        if (!s(status).isBlank() && !"0".equals(status)) { sql += " AND COALESCE(l.principal_approval,'')=?"; params.add(status); }
        return attachHeadNames(jdbc.queryForList(sql + " ORDER BY l.created_date DESC", params.toArray()), school, session);
    }

    private List<Map<String, Object>> attachHeadNames(List<Map<String, Object>> rows, String school, String session) {
        Map<String, String> names = new HashMap<>();
        for (Map<String, Object> item : wingIncharges(school, session)) names.put(s(item.get("staffUid")), s(item.get("name")));
        for (Map<String, Object> item : rows) item.put("headName", names.getOrDefault(s(item.get("headUid")), ""));
        return rows;
    }

    public Map<String, Object> updateHeadStatus(String school, String applicationId, String staff, String status, String remark) {
        jdbc.update("INSERT INTO faculty_leaveApplicationStatus(ApplicationId,staff_u_id,status,school_id,remark,created_date) VALUES(?,?,?,?,?,GETDATE())", applicationId, staff, status, school, remark);
        jdbc.update("UPDATE faculty_leaveapplicationform SET headofdepartment_approval=? WHERE ApplicationId=? AND school_id=?", status, applicationId, school);
        return row("message", "Record Saved Successfully");
    }

    public Map<String, Object> updatePrincipalStatus(String school, String applicationId, String staff, String status, String remark) {
        jdbc.update("INSERT INTO faculty_leaveApplicationStatus(ApplicationId,staff_u_id,status,school_id,remark,created_date) VALUES(?,?,?,?,?,GETDATE())", applicationId, staff, status, school, remark);
        jdbc.update("UPDATE faculty_leaveapplicationform SET principal_approval=? WHERE ApplicationId=? AND school_id=?", status, applicationId, school);
        jdbc.update("UPDATE faculty_leaveapplicationform SET headofdepartment_approval=? WHERE ApplicationId=? AND school_id=? AND EXISTS (SELECT 1 FROM desig_mapping dm WHERE dm.school_id=? AND dm.Staff_U_id=(SELECT TOP 1 headofdepartment FROM faculty_leaveapplicationform WHERE ApplicationId=? AND school_id=?) AND dm.Desg_id=64)", status, applicationId, school, school, applicationId, school);
        return row("message", "Record Saved Successfully");
    }

    public void createApplication(Map<String, Object> body, String school, String staff, String session, String medicalPath) {
        String applicationId = UUID.randomUUID().toString().replace("-", "").substring(0, 8) + System.currentTimeMillis();
        Integer applicationNo = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int,application_form_no)),0)+1 FROM faculty_leaveapplicationform WHERE school_id=?", Integer.class, school);
        jdbc.update("UPDATE facultyleaverecord SET status=0 WHERE staff_u_id=? AND school_id=?", staff, school);
        jdbc.update("INSERT INTO faculty_leaveapplicationform(application_form_no,Name,Department,NatureofLeaveapplied,Reason,DatesFrom,DatesTo,PreviousLeaveExtension,MedicalDate,IssueByDr,CompleteAddress,Date,Appointment_designation,ApplicationId,staff_u_id,school_id,headofdepartment,medicalcertificate,status,session,inchargeremark,date_leave) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,?,?,?)", applicationNo == null ? 1 : applicationNo, s(body.get("name")), s(body.get("department")), s(body.get("natureOfLeave")), s(body.get("reason")), s(body.get("datesFrom")), s(body.get("datesTo")), s(body.get("previousLeaveExtension")), s(body.get("medicalDate")), s(body.get("issueByDr")), s(body.get("completeAddress")), s(body.get("date")), s(body.get("appointment")), applicationId, staff, school, s(body.get("headOfDepartment")), medicalPath, session, s(body.get("inchargeRemark")), s(body.get("dateLeave")));
        jdbc.update("INSERT INTO faculty_leaveApplicationStatus(ApplicationId,staff_u_id,status,school_id,remark,created_date) VALUES(?,?,?,?,?,GETDATE())", applicationId, staff, "create", school, "");
        Object rawRows = body.get("leaveRows");
        if (rawRows instanceof List<?> rows) for (Object raw : rows) if (raw instanceof Map<?, ?> item) {
            String type = s(item.get("leaveTypeId"));
            if (type.isBlank() || s(item.get("leaveNowAppliedFor")).isBlank()) continue;
            jdbc.update("INSERT INTO facultyleaverecord(ApplicationId,leaveDue,leaveNowAppliedFor,BalanceIfSenctioned,leaveType,status,staff_u_id,school_id,created_date) VALUES(?,?,?,?,?,1,?,?,GETDATE())", applicationId, s(item.get("leaveDue")), s(item.get("leaveNowAppliedFor")), s(item.get("balanceIfSanctioned")), type, staff, school);
        }
    }
}
