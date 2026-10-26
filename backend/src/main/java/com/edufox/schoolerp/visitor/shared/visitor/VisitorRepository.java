package com.edufox.schoolerp.visitor.shared.visitor;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

/**
 * Direct-SQL repository for the legacy VMS tables. The queries preserve the
 * old page predicates and result shapes while keeping all workflow logic in
 * the Java service/repository layer.
 */
@Repository
@RequiredArgsConstructor
public class VisitorRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> visitorTypes() {
        return jdbc.queryForList("""
            SELECT VisitorTypeId AS id, TypeName AS label
            FROM dbo.VMS_VisitorType WHERE IsActive=1 ORDER BY TypeName
            """);
    }

    public List<Map<String, Object>> departments() {
        return jdbc.queryForList("""
            SELECT DepartmentId AS id, DepartmentName AS label
            FROM dbo.VMS_Department WHERE IsActive=1 ORDER BY DepartmentName
            """);
    }

    public List<Map<String, Object>> gates() {
        return jdbc.queryForList("""
            SELECT GateId AS id, GateName AS label
            FROM dbo.VMS_Gate WHERE IsActive=1 ORDER BY GateName
            """);
    }

    public List<Map<String, Object>> purposes(String schoolId) {
        return jdbc.queryForList("""
            SELECT PurposeId AS id, PurposeName AS label
            FROM dbo.VMS_PurposeOfVisit
            WHERE IsActive=1 AND school_id=? ORDER BY PurposeName
            """, schoolId);
    }

    /** Matches the legacy appointment page, which loads all active hosts. */
    public List<Map<String, Object>> hosts() {
        return jdbc.queryForList("""
            SELECT HostId AS id, HostName AS label, Designation AS role,
                   DepartmentId AS departmentId, school_id AS schoolId
            FROM dbo.VMS_Host WHERE IsActive=1 ORDER BY HostName
            """);
    }

    public Map<String, Object> config(String schoolId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT school_id AS schoolId, WalkinApprovalRequired AS walkinApprovalRequired,
              WalkinDirectEntryAllowed AS walkinDirectEntryAllowed,
              AppointmentApprovalRequired AS appointmentApprovalRequired,
              AppointmentDirectEntryAllowed AS appointmentDirectEntryAllowed,
              ModifiedOn AS modifiedOn, ModifiedBy AS modifiedBy
            FROM dbo.VMS_SchoolConfig WHERE school_id=?
            """, schoolId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<Map<String, Object>> searchVisitors(String schoolId, String term) {
        String like = "%" + (term == null ? "" : term.trim()) + "%";
        return jdbc.queryForList("""
            SELECT TOP 8 VisitorId AS id, FullName AS name, Mobile AS mobile,
              Email AS email, IdProofNumber AS idProofNumber, PhotoPath AS photoPath,
              [Address] AS address, VisitorTypeId AS visitorTypeId
            FROM dbo.VMS_Visitor
            WHERE school_id=? AND IsBlacklisted=0
              AND (FullName LIKE ? OR Mobile LIKE ?)
            ORDER BY FullName
            """, schoolId, like, like);
    }

    public List<Map<String, Object>> appointments(String schoolId, boolean todayOnly) {
        String datePredicate = todayOnly ? " AND v.VisitDate=CONVERT(date,GETDATE())" : "";
        return jdbc.queryForList("""
            SELECT v.VisitId AS visitId, v.PassNo AS passNo, vs.FullName AS fullName,
              vs.Mobile AS mobile, vs.Email AS email, vs.PhotoPath AS photoPath,
              h.HostName AS hostName, p.PurposeName AS purposeName,
              v.VisitDate AS visitDate, v.ExpectedEntry AS expectedEntry,
              v.ExpectedExit AS expectedExit, v.NoOfPersons AS noOfPersons,
              v.VehicleNo AS vehicleNo, v.Notes AS notes, v.VisitStatus AS visitStatus,
              v.ApprovalStatus AS approvalStatus, v.IsAppointment AS isAppointment,
              v.VisitorId AS visitorId, v.HostId AS hostId, v.DepartmentId AS departmentId,
              v.PurposeId AS purposeId, v.GateId AS gateId
            FROM dbo.VMS_Visit v
            JOIN dbo.VMS_Visitor vs ON vs.VisitorId=v.VisitorId AND vs.school_id=v.school_id
            JOIN dbo.VMS_Host h ON h.HostId=v.HostId AND h.school_id=v.school_id
            LEFT JOIN dbo.VMS_PurposeOfVisit p ON p.PurposeId=v.PurposeId AND p.school_id=v.school_id
            WHERE v.IsAppointment=1 AND v.VisitDate>=CONVERT(date,GETDATE())
              AND ISNULL(v.ApprovalStatus,'')<>'Rejected'
              AND ISNULL(v.VisitStatus,'')<>'Cancelled' AND v.school_id=?
            """ + datePredicate + " ORDER BY v.VisitDate,v.ExpectedEntry", schoolId);
    }

    public Map<String, Object> appointmentEdit(String schoolId, int visitId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT TOP 1 v.VisitId AS visitId, v.VisitDate AS visitDate,
              v.ExpectedEntry AS expectedEntry, v.ExpectedExit AS expectedExit,
              v.Notes AS notes, v.HostId AS hostId, v.PurposeId AS purposeId,
              v.DepartmentId AS departmentId, v.GateId AS gateId,
              vs.VisitorId AS visitorId, vs.FullName AS fullName, vs.Mobile AS mobile,
              vs.Email AS email, vs.[Address] AS address, vs.PhotoPath AS photoPath,
              vs.VisitorRefId AS visitorRefId
            FROM dbo.VMS_Visit v JOIN dbo.VMS_Visitor vs
              ON vs.VisitorId=v.VisitorId AND vs.school_id=v.school_id
            WHERE v.VisitId=? AND v.school_id=?
            """, visitId, schoolId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public Map<String, Object> visitDetails(String schoolId, int visitId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT TOP 1 v.VisitId AS visitId,v.PassNo AS passNo,vs.FullName AS fullName,
              vs.Mobile AS mobile,vs.Email AS email,vs.Gender AS gender,vs.DOB AS dob,
              vs.PhotoPath AS photoPath,vs.IdProofType AS idProofType,
              vs.IdProofNumber AS idProofNumber,vs.[Address] AS address,
              t.TypeName AS typeName,h.HostName AS hostName,d.DepartmentName AS departmentName,
              p.PurposeName AS purposeName,g.GateName AS gateName,v.VisitDate AS visitDate,
              v.ExpectedEntry AS expectedEntry,v.ExpectedExit AS expectedExit,
              v.NoOfPersons AS noOfPersons,v.VehicleNo AS vehicleNo,v.Notes AS notes,
              v.CheckInTime AS checkInTime,v.CheckOutTime AS checkOutTime,
              v.VisitStatus AS visitStatus,v.ApprovalStatus AS approvalStatus,
              v.IsAppointment AS isAppointment,v.RejectReason AS rejectReason
            FROM dbo.VMS_Visit v JOIN dbo.VMS_Visitor vs ON vs.VisitorId=v.VisitorId
            LEFT JOIN dbo.VMS_VisitorType t ON t.VisitorTypeId=v.VisitorTypeId
            LEFT JOIN dbo.VMS_Host h ON h.HostId=v.HostId
            LEFT JOIN dbo.VMS_Department d ON d.DepartmentId=v.DepartmentId
            LEFT JOIN dbo.VMS_PurposeOfVisit p ON p.PurposeId=v.PurposeId
            LEFT JOIN dbo.VMS_Gate g ON g.GateId=v.GateId
            WHERE v.VisitId=? AND v.school_id=?
            """, visitId, schoolId);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public int saveHost(String schoolId, String name, Integer departmentId, String role) {
        List<Integer> ids = jdbc.query("""
            SELECT TOP 1 HostId FROM dbo.VMS_Host
            WHERE HostName=? AND school_id=? ORDER BY HostId
            """, (rs, row) -> rs.getInt(1), name, schoolId);
        if (!ids.isEmpty()) {
            jdbc.update("""
                UPDATE dbo.VMS_Host SET DepartmentId=?,Designation=?,IsActive=1
                WHERE HostName=? AND school_id=?
                """, departmentId, blankNull(role), name, schoolId);
            return ids.get(0);
        }
        jdbc.update("""
            INSERT INTO dbo.VMS_Host(HostName,DepartmentId,Designation,IsActive,school_id)
            VALUES(?,?,?,1,?)
            """, name, departmentId, blankNull(role), schoolId);
        return jdbc.queryForObject("""
            SELECT TOP 1 HostId FROM dbo.VMS_Host
            WHERE HostName=? AND school_id=? ORDER BY HostId DESC
            """, Integer.class, name, schoolId);
    }

    public boolean isBlacklisted(String mobile, String idProofNumber) {
        return jdbc.queryForObject("""
            SELECT CASE WHEN EXISTS(SELECT 1 FROM dbo.VMS_Blacklist
              WHERE IsActive=1 AND (Mobile=? OR (IdProofNumber=? AND ? IS NOT NULL)))
              THEN 1 ELSE 0 END
            """, Integer.class, mobile, blankNull(idProofNumber), blankNull(idProofNumber)) == 1;
    }

    public int upsertVisitor(String schoolId, Integer visitorId, Integer visitorTypeId, String fullName,
                             String mobile, String email, String gender, LocalDate dob, String idProofType,
                             String idProofNumber, String photoPath, String address, Integer createdBy,
                             String visitorRefId) {
        int existing = visitorId == null ? 0 : visitorId;
        if (existing == 0) {
            List<Integer> ids = jdbc.query("""
                SELECT TOP 1 VisitorId FROM dbo.VMS_Visitor
                WHERE Mobile=? AND school_id=? ORDER BY VisitorId DESC
                """, (rs, row) -> rs.getInt(1), mobile, schoolId);
            if (!ids.isEmpty()) existing = ids.get(0);
        }
        if (existing == 0) {
            int id = jdbc.queryForObject("""
                INSERT INTO dbo.VMS_Visitor(VisitorTypeId,FullName,Mobile,Email,Gender,DOB,
                  IdProofType,IdProofNumber,PhotoPath,[Address],CreatedBy,VisitorRefId,school_id)
                OUTPUT INSERTED.VisitorId
                VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)
                """, Integer.class, zeroNull(visitorTypeId), fullName, mobile, blankNull(email), blankNull(gender),
                dob == null ? null : Date.valueOf(dob), blankNull(idProofType), blankNull(idProofNumber),
                blankNull(photoPath), blankNull(address), createdBy, blankNull(visitorRefId), schoolId);
            jdbc.update("UPDATE dbo.VMS_Visitor SET VisitorCode='VC-'+CONVERT(varchar(4),YEAR(GETDATE()))+'-'+RIGHT('000000'+CONVERT(varchar(6),?),6) WHERE VisitorId=? AND school_id=?", id, id, schoolId);
            return id;
        }
        int updated = jdbc.update("""
            UPDATE dbo.VMS_Visitor SET VisitorTypeId=?,FullName=?,Email=?,Gender=?,DOB=?,
              IdProofType=COALESCE(NULLIF(LTRIM(RTRIM(?)),''),IdProofType),
              IdProofNumber=COALESCE(?,IdProofNumber),PhotoPath=COALESCE(NULLIF(LTRIM(RTRIM(?)),''),PhotoPath),
              [Address]=?,VisitorRefId=COALESCE(NULLIF(LTRIM(RTRIM(?)),''),VisitorRefId)
            WHERE VisitorId=? AND school_id=?
            """, zeroNull(visitorTypeId), fullName, blankNull(email), blankNull(gender),
            dob == null ? null : Date.valueOf(dob), blankNull(idProofType), blankNull(idProofNumber),
            blankNull(photoPath), blankNull(address), blankNull(visitorRefId), existing, schoolId);
        if (updated != 1) throw new IllegalStateException("Visitor could not be updated.");
        return existing;
    }

    public String generatePassNo() {
        jdbc.update("IF NOT EXISTS(SELECT 1 FROM dbo.VMS_PassSetting) INSERT INTO dbo.VMS_PassSetting DEFAULT VALUES");
        jdbc.update("UPDATE TOP(1) dbo.VMS_PassSetting SET RunningNumber=RunningNumber+1");
        return jdbc.queryForObject("""
            SELECT TOP 1 REPLACE(REPLACE(PassNoFormat,'{YYYY}',CONVERT(varchar(4),YEAR(GETDATE()))),
              '{NNNNNN}',RIGHT('000000'+CONVERT(varchar(6),RunningNumber),6))
            FROM dbo.VMS_PassSetting
            """, String.class);
    }

    public int insertVisit(String schoolId, int visitorId, Integer departmentId, Integer purposeId, Integer hostId,
                           Integer gateId, LocalDate visitDate, Timestamp expectedEntry, Timestamp expectedExit,
                           int persons, String vehicleNo, String notes, boolean appointment, Integer createdBy) {
        String approval = jdbc.queryForObject("SELECT TOP 1 CASE WHEN ApprovalRequired=0 THEN 'Approved' ELSE 'Pending' END FROM dbo.VMS_SystemSetting", String.class);
        if (approval == null) approval = "Approved";
        String passNo = generatePassNo();
        return jdbc.queryForObject("""
            INSERT INTO dbo.VMS_Visit(PassNo,VisitorId,VisitorTypeId,PurposeId,HostId,DepartmentId,GateId,
              VisitDate,ExpectedEntry,ExpectedExit,NoOfPersons,VehicleNo,Notes,IsAppointment,CreatedBy,
              ApprovalStatus,VisitStatus,school_id)
            OUTPUT INSERTED.VisitId
            VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'Scheduled',?)
            """, Integer.class, passNo, visitorId, null, purposeId, hostId, departmentId, gateId, Date.valueOf(visitDate),
            expectedEntry, expectedExit, persons, blankNull(vehicleNo), blankNull(notes), appointment, createdBy,
            approval, schoolId);
    }

    public int updateVisit(String schoolId, int visitId, Integer departmentId, Integer purposeId, Integer hostId,
                           Integer gateId, LocalDate visitDate, Timestamp expectedEntry, Timestamp expectedExit,
                           int persons, String vehicleNo, String notes) {
        return jdbc.update("""
            UPDATE dbo.VMS_Visit SET PurposeId=?,HostId=?,DepartmentId=?,GateId=?,VisitDate=?,
              ExpectedEntry=?,ExpectedExit=?,NoOfPersons=?,VehicleNo=?,Notes=?
            WHERE VisitId=? AND school_id=?
            """, purposeId, hostId, departmentId, gateId, Date.valueOf(visitDate), expectedEntry, expectedExit,
            persons, blankNull(vehicleNo), blankNull(notes), visitId, schoolId);
    }

    public int cancelVisit(String schoolId, int visitId, String reason) {
        return jdbc.update("""
            UPDATE dbo.VMS_Visit SET ApprovalStatus='Rejected',ApprovedOn=GETDATE(),RejectReason=?
            WHERE VisitId=? AND school_id=?
            """, reason, visitId, schoolId);
    }

    public Map<String, Object> approvalSummary(String schoolId) {
        List<Map<String,Object>> rows = jdbc.queryForList("""
            SELECT ISNULL(SUM(CASE WHEN ApprovalStatus='Pending' THEN 1 ELSE 0 END),0) AS pending,
              ISNULL(SUM(CASE WHEN ApprovalStatus='Approved' AND CONVERT(date,ApprovedOn)=CONVERT(date,GETDATE()) THEN 1 ELSE 0 END),0) AS approvedToday,
              ISNULL(SUM(CASE WHEN ApprovalStatus='Rejected' AND CONVERT(date,ApprovedOn)=CONVERT(date,GETDATE()) THEN 1 ELSE 0 END),0) AS rejectedToday,
              ISNULL(SUM(CASE WHEN CONVERT(date,VisitDate)=CONVERT(date,GETDATE()) THEN 1 ELSE 0 END),0) AS totalVisitorsToday
            FROM dbo.VMS_Visit WHERE school_id=?
            """, schoolId);
        return rows.isEmpty() ? new LinkedHashMap<>() : rows.get(0);
    }

    public List<Map<String, Object>> approvalRows(String schoolId, String search, Integer visitorTypeId,
                                                    Integer departmentId, LocalDate visitDate, String status) {
        StringBuilder sql = new StringBuilder("""
            SELECT v.VisitId AS visitId,v.PassNo AS passNo,vs.FullName AS fullName,vs.Mobile AS mobile,
              vs.PhotoPath AS photoPath,t.TypeName AS typeName,h.HostName AS hostName,p.PurposeName AS purposeName,
              v.ExpectedEntry AS expectedEntry,v.VisitDate AS visitDate,v.ApprovalStatus AS approvalStatus,
              v.IsAppointment AS isAppointment,v.NoOfPersons AS noOfPersons,v.Notes AS notes
            FROM dbo.VMS_Visit v JOIN dbo.VMS_Visitor vs ON vs.VisitorId=v.VisitorId AND vs.school_id=v.school_id
            LEFT JOIN dbo.VMS_VisitorType t ON t.VisitorTypeId=v.VisitorTypeId
            JOIN dbo.VMS_Host h ON h.HostId=v.HostId AND h.school_id=v.school_id
            LEFT JOIN dbo.VMS_PurposeOfVisit p ON p.PurposeId=v.PurposeId
            WHERE v.school_id=? AND (?='' OR v.ApprovalStatus=?)
            """);
        List<Object> args = new ArrayList<>(List.of(schoolId, clean(status), clean(status)));
        if (visitorTypeId != null) { sql.append(" AND v.VisitorTypeId=?"); args.add(visitorTypeId); }
        if (departmentId != null) { sql.append(" AND v.DepartmentId=?"); args.add(departmentId); }
        if (visitDate != null) { sql.append(" AND v.VisitDate=?"); args.add(Date.valueOf(visitDate)); }
        String term = clean(search);
        sql.append(" AND (?='' OR vs.FullName LIKE '%' + ? + '%' OR vs.Mobile LIKE '%' + ? + '%' OR v.PassNo LIKE '%' + ? + '%')");
        args.add(term); args.add(term); args.add(term); args.add(term);
        sql.append(" ORDER BY v.CreatedOn DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public int setApproval(String schoolId, int visitId, String status, Integer userId, String reason) {
        return jdbc.update("""
            UPDATE dbo.VMS_Visit SET ApprovalStatus=?,ApprovedBy=?,ApprovedOn=GETDATE(),RejectReason=?
            WHERE VisitId=? AND school_id=?
            """, status, userId, blankNull(reason), visitId, schoolId);
    }

    public List<Map<String, Object>> historyRows(String schoolId, LocalDate from, LocalDate to, String search) {
        StringBuilder sql = new StringBuilder("""
            SELECT v.VisitId AS visitId,v.PassNo AS passNo,vs.FullName AS fullName,vs.Mobile AS mobile,
              vs.PhotoPath AS photoPath,t.TypeName AS typeName,h.HostName AS hostName,p.PurposeName AS purposeName,
              v.VisitDate AS visitDate,
              CASE WHEN v.IsAppointment=1 THEN CONVERT(varchar(100),v.ExpectedEntry,100) ELSE '' END AS scheduledDate,
              COALESCE(v.CheckInTime,CASE WHEN v.IsAppointment=1 THEN v.ExpectedEntry ELSE NULL END) AS entryTime,
              v.CheckInTime AS checkInTime,v.CheckOutTime AS checkOutTime,v.VisitStatus AS visitStatus,v.ApprovalStatus AS approvalStatus,
              v.IsAppointment AS isAppointment,v.ExpectedEntry AS expectedEntry,v.ExpectedExit AS expectedExit,
              v.NoOfPersons AS noOfPersons,vs.[Address] AS address,v.Notes AS notes
            FROM dbo.VMS_Visit v JOIN dbo.VMS_Visitor vs ON vs.VisitorId=v.VisitorId AND vs.school_id=v.school_id
            LEFT JOIN dbo.VMS_VisitorType t ON t.VisitorTypeId=v.VisitorTypeId
            JOIN dbo.VMS_Host h ON h.HostId=v.HostId AND h.school_id=v.school_id
            LEFT JOIN dbo.VMS_PurposeOfVisit p ON p.PurposeId=v.PurposeId
            WHERE v.school_id=?
            """);
        List<Object> args = new ArrayList<>(List.of(schoolId));
        if (from != null) { sql.append(" AND v.VisitDate>=?"); args.add(Date.valueOf(from)); }
        if (to != null) { sql.append(" AND v.VisitDate<=?"); args.add(Date.valueOf(to)); }
        String term = clean(search);
        sql.append(" AND (?='' OR vs.FullName LIKE '%' + ? + '%' OR vs.Mobile LIKE '%' + ? + '%' OR v.PassNo LIKE '%' + ? + '%' OR h.HostName LIKE '%' + ? + '%' OR p.PurposeName LIKE '%' + ? + '%')");
        args.add(term); for (int i=0;i<5;i++) args.add(term);
        sql.append(" ORDER BY v.VisitDate DESC,v.VisitId DESC");
        return jdbc.queryForList(sql.toString(), args.toArray());
    }

    public Map<String, Object> dashboardStats(String schoolId) {
        List<Map<String,Object>> rows = jdbc.queryForList("""
            SELECT (SELECT COUNT(*) FROM dbo.VMS_Visit WHERE VisitDate=CONVERT(date,GETDATE()) AND school_id=?) AS todaysVisitors,
              (SELECT COUNT(*) FROM dbo.VMS_Visit WHERE VisitStatus='CheckedIn' AND school_id=?) AS currentlyInside,
              (SELECT COUNT(*) FROM dbo.VMS_Visit WHERE VisitStatus='CheckedOut' AND VisitDate=CONVERT(date,GETDATE()) AND school_id=?) AS checkedOut,
              (SELECT COUNT(*) FROM dbo.VMS_Visit WHERE IsAppointment=1 AND VisitDate=CONVERT(date,GETDATE()) AND school_id=?) AS appointmentsToday,
              (SELECT COUNT(*) FROM dbo.VMS_Visit WHERE ApprovalStatus='Pending' AND school_id=?) AS pendingApprovals
            """, schoolId, schoolId, schoolId, schoolId, schoolId);
        return rows.isEmpty() ? new LinkedHashMap<>() : rows.get(0);
    }

    public List<Map<String,Object>> dashboardTrend(String schoolId) {
        return jdbc.queryForList("""
            SELECT DATEPART(HOUR,COALESCE(ExpectedEntry,CreatedOn)) AS hour,COUNT(*) AS total
            FROM dbo.VMS_Visit WHERE VisitDate=CONVERT(date,GETDATE()) AND school_id=?
            GROUP BY DATEPART(HOUR,COALESCE(ExpectedEntry,CreatedOn))
            ORDER BY hour
            """, schoolId);
    }

    public List<Map<String,Object>> inside(String schoolId) {
        return jdbc.queryForList("""
            SELECT v.VisitId AS visitId,v.PassNo AS passNo,vs.FullName AS fullName,vs.PhotoPath AS photoPath,
              h.HostName AS hostName,d.DepartmentName AS departmentName,p.PurposeName AS purposeName,
              v.CheckInTime AS checkInTime,v.ExpectedExit AS expectedExit,g.GateName AS gateName
            FROM dbo.VMS_Visit v JOIN dbo.VMS_Visitor vs ON vs.VisitorId=v.VisitorId AND vs.school_id=v.school_id
            LEFT JOIN dbo.VMS_Host h ON h.HostId=v.HostId AND h.school_id=v.school_id
            LEFT JOIN dbo.VMS_Department d ON d.DepartmentId=v.DepartmentId
            LEFT JOIN dbo.VMS_PurposeOfVisit p ON p.PurposeId=v.PurposeId AND p.school_id=v.school_id
            LEFT JOIN dbo.VMS_Gate g ON g.GateId=v.GateId
            WHERE v.VisitStatus='CheckedIn' AND v.school_id=? ORDER BY v.CheckInTime DESC
            """, schoolId);
    }

    public Map<String,Object> lookupAppointment(String schoolId, String refNo) {
        List<Map<String,Object>> rows = jdbc.queryForList("""
            SELECT TOP 1 v.VisitId AS visitId,v.PassNo AS refNo,vs.FullName AS visitorName,vs.Mobile AS mobileNo,
              h.HostName AS hostName,d.DepartmentName AS departmentName,p.PurposeName AS purposeName,
              v.VisitDate AS appointmentDate,v.ExpectedEntry AS appointmentTime
            FROM dbo.VMS_Visit v JOIN dbo.VMS_Visitor vs ON vs.VisitorId=v.VisitorId AND vs.school_id=v.school_id
            LEFT JOIN dbo.VMS_Host h ON h.HostId=v.HostId AND h.school_id=v.school_id
            LEFT JOIN dbo.VMS_Department d ON d.DepartmentId=v.DepartmentId
            LEFT JOIN dbo.VMS_PurposeOfVisit p ON p.PurposeId=v.PurposeId AND p.school_id=v.school_id
            WHERE v.school_id=? AND v.IsAppointment=1 AND (v.PassNo=? OR CONVERT(varchar(30),v.VisitId)=?)
            ORDER BY v.VisitId DESC
            """, schoolId, refNo, refNo);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public void saveConfig(String schoolId, boolean walkinApproval, boolean walkinDirect,
                           boolean appointmentApproval, boolean appointmentDirect, Integer modifiedBy) {
        if (walkinApproval == walkinDirect || appointmentApproval == appointmentDirect)
            throw new IllegalArgumentException("Approval and direct-entry options cannot both have the same value.");
        jdbc.update("""
            IF EXISTS(SELECT 1 FROM dbo.VMS_SchoolConfig WHERE school_id=?)
              UPDATE dbo.VMS_SchoolConfig SET WalkinApprovalRequired=?,WalkinDirectEntryAllowed=?,
                AppointmentApprovalRequired=?,AppointmentDirectEntryAllowed=?,ModifiedOn=GETDATE(),ModifiedBy=?
                WHERE school_id=?
            ELSE
              INSERT INTO dbo.VMS_SchoolConfig(school_id,WalkinApprovalRequired,WalkinDirectEntryAllowed,
                AppointmentApprovalRequired,AppointmentDirectEntryAllowed,ModifiedOn,ModifiedBy)
              VALUES(?,?,?,?,?,GETDATE(),?)
            """, schoolId, walkinApproval, walkinDirect, appointmentApproval, appointmentDirect, modifiedBy, schoolId,
            schoolId, walkinApproval, walkinDirect, appointmentApproval, appointmentDirect, modifiedBy);
    }

    private static String clean(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static String blankNull(String value) { return value == null || value.trim().isEmpty() ? null : value.trim(); }
    private static Integer zeroNull(Integer value) { return value == null || value <= 0 ? null : value; }
}
