package com.edufox.schoolerp.ptm;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class PtmRepository {
    private final JdbcTemplate jdbc;

    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static int integer(Object value) { try { return Integer.parseInt(text(value)); } catch (Exception ignored) { return 0; } }

    public List<Map<String, Object>> sessions(String schoolId) {
        return jdbc.queryForList("""
            SELECT session AS value, session AS label, status
            FROM TB_Session WHERE school_id=?
            ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END, session DESC
            """, schoolId);
    }

    public String currentSession(String schoolId) {
        List<String> values = jdbc.query("""
            SELECT TOP 1 session FROM TB_Session WHERE school_id=?
            ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END, session DESC
            """, (rs, row) -> rs.getString(1), schoolId);
        return values.isEmpty() ? "" : text(values.get(0));
    }

    /**
     * Direct-SQL equivalent of the legacy BindStaffClasses call used by
     * PTMMaster. Desg 28/39 are the same full-access designations used by the
     * legacy procedure; other staff see their session class-teacher sections.
     */
    public List<Map<String, Object>> classesForStaff(String schoolId, String session, String staffUid) {
        return jdbc.queryForList("""
            WITH ActiveSections AS (
                SELECT DISTINCT TRY_CONVERT(int, csm.sec_id) AS sec_id
                FROM ClassSessionMapping csm
                WHERE csm.school_id=? AND csm.status=1
                  AND csm.From_Year <= TRY_CONVERT(int, LEFT(?,4))
                  AND (csm.To_Year=0 OR csm.To_Year >= TRY_CONVERT(int, RIGHT(?,4)))
            ), FullAccess AS (
                SELECT CASE WHEN EXISTS (
                    SELECT 1 FROM desig_mapping d
                    WHERE d.school_id=? AND d.Staff_U_id=? AND d.Desg_id IN (28,39)
                ) THEN 1 ELSE 0 END AS allowed
            )
            SELECT DISTINCT cn.class_name AS classes,
                   cn.class_name + ' (' + tc.section + ')' AS label,
                   tc.sec_id AS secId, tc.class_id AS classId
            FROM TB_Class tc
            JOIN class_name cn ON cn.school_id=tc.school_id AND cn.id=tc.class_id
            CROSS JOIN FullAccess fa
            WHERE tc.school_id=?
              AND EXISTS (SELECT 1 FROM ActiveSections a WHERE a.sec_id=tc.sec_id)
              AND (
                    fa.allowed=1
                    OR EXISTS (
                        SELECT 1 FROM Tb_ClassTeacher ct
                        WHERE ct.school_id=tc.school_id AND ct.section_id=tc.sec_id
                          AND ct.session=? AND CAST(ct.staff_u_id AS varchar(50))=?
                    )
                  )
            ORDER BY tc.class_id, tc.sec_id
            """, schoolId, session, session, schoolId, staffUid, schoolId, session, staffUid);
    }

    public List<Map<String, Object>> sectionsForStaff(String schoolId, String session, String staffUid, Collection<?> classIds) {
        List<Object> args = new ArrayList<>(List.of(schoolId, session, session, schoolId, staffUid, schoolId, session, staffUid));
        String in = placeholders(classIds, args);
        String classFilter = in.isBlank() ? "" : " AND tc.class_id IN (" + in + ")";
        String sql = """
            WITH ActiveSections AS (
                SELECT DISTINCT TRY_CONVERT(int, csm.sec_id) AS sec_id
                FROM ClassSessionMapping csm
                WHERE csm.school_id=? AND csm.status=1
                  AND csm.From_Year <= TRY_CONVERT(int, LEFT(?,4))
                  AND (csm.To_Year=0 OR csm.To_Year >= TRY_CONVERT(int, RIGHT(?,4)))
            ), FullAccess AS (
                SELECT CASE WHEN EXISTS (
                    SELECT 1 FROM desig_mapping d
                    WHERE d.school_id=? AND d.Staff_U_id=? AND d.Desg_id IN (28,39)
                ) THEN 1 ELSE 0 END AS allowed
            )
            SELECT DISTINCT cn.class_name AS className,
                   cn.class_name + ' (' + tc.section + ')' AS label,
                   tc.sec_id AS secId, tc.class_id AS classId
            FROM TB_Class tc
            JOIN class_name cn ON cn.school_id=tc.school_id AND cn.id=tc.class_id
            CROSS JOIN FullAccess fa
            WHERE tc.school_id=?
              AND EXISTS (SELECT 1 FROM ActiveSections a WHERE a.sec_id=tc.sec_id)
              AND (
                    fa.allowed=1
                    OR EXISTS (
                        SELECT 1 FROM Tb_ClassTeacher ct
                        WHERE ct.school_id=tc.school_id AND ct.section_id=tc.sec_id
                          AND ct.session=? AND CAST(ct.staff_u_id AS varchar(50))=?
                    )
                  )
            """ + classFilter + " ORDER BY tc.class_id, tc.sec_id";
        return jdbc.queryForList(sql, args.toArray());
    }

    public List<Map<String, Object>> masterList(String schoolId) {
        return jdbc.queryForList("""
            SELECT m.PTMId AS ptmId, m.PTMName AS ptmName, m.Session AS session,
                   CONVERT(varchar(10),m.PTMDate,23) AS ptmDate,
                   m.StartTime AS startTime, m.EndTime AS endTime,
                   m.MeetingType AS meetingType, m.Venue AS venue,
                   m.Status AS status, m.Remarks AS remarks,
                   COALESCE(STUFF((SELECT ', ' + c.ClassName FROM PTM_Class c
                       WHERE c.PTMId=m.PTMId ORDER BY c.ClassName
                       FOR XML PATH(''), TYPE).value('.','nvarchar(max)'),1,2,''),'') AS mappedClasses
            FROM PTM_Master m WHERE m.SchoolId=?
            ORDER BY m.PTMDate DESC, m.PTMId DESC
            """, schoolId);
    }

    public Map<String, Object> master(String schoolId, int ptmId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT PTMId AS ptmId,SchoolId AS schoolId,Session AS session,PTMName AS ptmName,
                   CONVERT(varchar(10),PTMDate,23) AS ptmDate,StartTime AS startTime,EndTime AS endTime,
                   MeetingType AS meetingType,Venue AS venue,Status AS status,Remarks AS remarks,
                   CreatedBy AS createdBy,CreatedDate AS createdDate,UpdatedDate AS updatedDate
            FROM PTM_Master WHERE PTMId=? AND SchoolId=?
            """, ptmId, schoolId);
        if (rows.isEmpty()) return null;
        Map<String, Object> result = new LinkedHashMap<>(rows.get(0));
        result.put("classIds", jdbc.queryForList("SELECT ClassId FROM PTM_Class WHERE PTMId=? ORDER BY ClassId", Integer.class, ptmId));
        result.put("sectionIds", jdbc.queryForList("SELECT SecId FROM PTM_Section WHERE PTMId=? ORDER BY SecId", Integer.class, ptmId));
        return result;
    }

    public int insertMaster(String schoolId, String session, String name, String date, String start, String end,
                            String meetingType, String venue, String status, String remarks, String createdBy) {
        Integer id = jdbc.queryForObject("""
            INSERT INTO PTM_Master(SchoolId,Session,PTMName,PTMDate,StartTime,EndTime,MeetingType,Venue,Status,Remarks,CreatedBy,CreatedDate)
            OUTPUT INSERTED.PTMId VALUES(?,?,?,?,?,?,?,?,?,?,?,GETDATE())
            """, Integer.class, schoolId, session, name, date, start, end, meetingType, blankNull(venue), status, blankNull(remarks), blankNull(createdBy));
        return id == null ? 0 : id;
    }

    public int updateMaster(int ptmId, String schoolId, String session, String name, String date, String start, String end,
                            String meetingType, String venue, String status, String remarks) {
        return jdbc.update("""
            UPDATE PTM_Master SET Session=?,PTMName=?,PTMDate=?,StartTime=?,EndTime=?,MeetingType=?,Venue=?,Status=?,Remarks=?,UpdatedDate=GETDATE()
            WHERE PTMId=? AND SchoolId=?
            """, session, name, date, start, end, meetingType, blankNull(venue), status, blankNull(remarks), ptmId, schoolId);
    }

    public void deleteClassMappings(int ptmId) {
        jdbc.update("DELETE FROM PTM_TeacherAssignment WHERE PTMId=?", ptmId);
        jdbc.update("DELETE FROM PTM_Student WHERE PTMId=?", ptmId);
        jdbc.update("DELETE FROM PTM_Class WHERE PTMId=?", ptmId);
    }

    public void replaceClasses(int ptmId, Collection<?> classIds, List<Map<String, Object>> available) {
        Map<Integer, String> names = new HashMap<>();
        for (Map<String, Object> row : available) names.put(integer(row.get("classId")), text(row.get("classes")));
        for (Object raw : classIds) {
            int id = integer(raw); if (id <= 0 || !names.containsKey(id)) continue;
            jdbc.update("INSERT INTO PTM_Class(PTMId,ClassId,ClassName,CreatedDate) SELECT ?,?,?,GETDATE() WHERE NOT EXISTS(SELECT 1 FROM PTM_Class WHERE PTMId=? AND ClassId=?)",
                ptmId, id, names.get(id), ptmId, id);
        }
    }

    public void deleteSectionMappings(int ptmId) { jdbc.update("DELETE FROM PTM_Section WHERE PTMId=?", ptmId); }

    public void replaceSections(int ptmId, Collection<?> sectionIds, List<Map<String, Object>> available) {
        Map<Integer, String> names = new HashMap<>();
        for (Map<String, Object> row : available) names.put(integer(row.get("secId")), text(row.get("label")));
        for (Object raw : sectionIds) {
            int id = integer(raw); if (id <= 0 || !names.containsKey(id)) continue;
            jdbc.update("INSERT INTO PTM_Section(PTMId,SecId,SectionName,CreatedDate) SELECT ?,?,?,GETDATE() WHERE NOT EXISTS(SELECT 1 FROM PTM_Section WHERE PTMId=? AND SecId=?)",
                ptmId, id, names.get(id), ptmId, id);
        }
    }

    public void insertStudentMappings(int ptmId, String schoolId, String session) {
        jdbc.update("""
            INSERT INTO PTM_Student(PTMId,SchoolId,Session,StudentId,ClassId,secid)
            SELECT ?,s.SCHOOL_ID,pm.Session,s.Registration_NO,s.class_id,s.sec_id
            FROM TB_StudentMaster s JOIN PTM_Master pm ON pm.PTMId=?
            JOIN PTM_Class pc ON pc.PTMId=? AND pc.ClassId=s.class_id
            JOIN PTM_Section ps ON ps.PTMId=? AND ps.SecId=s.sec_id
            WHERE s.SCHOOL_ID=? AND s.session=?
              AND NOT EXISTS(SELECT 1 FROM PTM_Student x WHERE x.PTMId=? AND x.StudentId=s.Registration_NO)
            """, ptmId, ptmId, ptmId, ptmId, schoolId, session, ptmId);
    }

    public void updateStudentMappings(int ptmId, String schoolId, String session) {
        jdbc.update("""
            DELETE ps FROM PTM_Student ps
            WHERE ps.PTMId=? AND ps.SchoolId=?
              AND NOT EXISTS(
                SELECT 1 FROM TB_StudentMaster s
                JOIN PTM_Class pc ON pc.PTMId=? AND pc.ClassId=s.class_id
                JOIN PTM_Section px ON px.PTMId=? AND px.SecId=s.sec_id
                WHERE s.Registration_NO=ps.StudentId AND s.SCHOOL_ID=? AND s.session=?
              )
            """, ptmId, schoolId, ptmId, ptmId, schoolId, session);
        insertStudentMappings(ptmId, schoolId, session);
    }

    public List<Map<String, Object>> reportRows(String schoolId, String session, String status, String search) {
        return jdbc.queryForList("""
            SELECT pm.PTMId AS ptmId,pm.PTMName AS ptmName,pm.Session AS session,
              CONVERT(varchar(10),pm.PTMDate,23) AS ptmDate,pm.StartTime AS startTime,pm.EndTime AS endTime,
              pm.MeetingType AS meetingType,pm.Venue AS venue,pm.Status AS status,pm.Remarks AS remarks,
              COALESCE(c.ClassNames,'') AS classNames,COALESCE(s.TotalStudents,0) AS totalStudents,
              COALESCE(s.CompletedMeetings,0) AS completedMeetings,COALESCE(s.PresentMeetings,0) AS presentMeetings,
              COALESCE(s.ParentAbsent,0) AS parentAbsent,COALESCE(s.PendingMeetings,0) AS pendingMeetings
            FROM PTM_Master pm
            OUTER APPLY(SELECT STRING_AGG(CONVERT(varchar(max),pc.ClassName),', ') WITHIN GROUP(ORDER BY pc.ClassName) AS ClassNames FROM PTM_Class pc WHERE pc.PTMId=pm.PTMId)c
            OUTER APPLY(SELECT COUNT(*) AS TotalStudents,
              CASE WHEN SUM(CASE WHEN ISNULL(ps.MeetingStatus,'')='Completed' THEN 1 ELSE 0 END)>0 THEN 1 ELSE 0 END AS CompletedMeetings,
              SUM(CASE WHEN ISNULL(ps.ParentAttendance,0)=1 THEN 1 ELSE 0 END) AS PresentMeetings,
              SUM(CASE WHEN ISNULL(ps.MeetingStatus,'') IN('Absent','Parent Absent') THEN 1 ELSE 0 END) AS ParentAbsent,
              SUM(CASE WHEN ISNULL(ps.MeetingStatus,'')='Pending' THEN 1 ELSE 0 END) AS PendingMeetings
              FROM PTM_Student ps WHERE ps.PTMId=pm.PTMId)s
            WHERE pm.SchoolId=? AND (ISNULL(?,'')='' OR pm.Session=?)
              AND (ISNULL(?,'')='' OR pm.Status=?)
              AND (ISNULL(?,'')='' OR pm.PTMName LIKE '%' + ? + '%')
            ORDER BY pm.PTMDate DESC,pm.StartTime DESC,pm.PTMId DESC
            """, schoolId, session, session, status, status, search, search);
    }

    public List<Map<String, Object>> facultySummary(String schoolId, String session) {
        return jdbc.queryForList("""
            WITH TeacherMeetings AS(
              SELECT DISTINCT ta.PTMTeacherId,ta.TeacherId,ta.TeacherName,ta.Subject,ta.PTMId,ta.SecId,m.PTMDate,
                CASE WHEN m.Status='Cancelled' OR ta.Status='Cancelled' THEN 'Cancelled'
                  WHEN EXISTS(SELECT 1 FROM PTM_Student ps WHERE ps.PTMId=ta.PTMId AND ps.secid=ta.SecId AND ps.SchoolId=? )
                   AND NOT EXISTS(SELECT 1 FROM PTM_Student ps WHERE ps.PTMId=ta.PTMId AND ps.secid=ta.SecId AND ps.SchoolId=? AND ISNULL(LTRIM(RTRIM(ps.MeetingStatus)),'')<>'Completed')
                  THEN 'Completed' ELSE ISNULL(NULLIF(LTRIM(RTRIM(ta.Status)),''),m.Status) END AS MeetingStatus
              FROM PTM_TeacherAssignment ta JOIN PTM_Master m ON m.PTMId=ta.PTMId
              WHERE m.SchoolId=? AND (ISNULL(?,'')='' OR m.Session=?)
            ), TeacherSummary AS(
              SELECT TeacherId,MAX(TeacherName) AS TeacherName,MAX(Subject) AS Subject,COUNT(DISTINCT PTMId) AS TotalMeetings,
                COUNT(DISTINCT CASE WHEN MeetingStatus='Completed' THEN PTMId END) AS Completed,
                COUNT(DISTINCT CASE WHEN MeetingStatus IN('Published','Scheduled','Draft') THEN PTMId END) AS Pending,
                COUNT(DISTINCT CASE WHEN MeetingStatus='Cancelled' THEN PTMId END) AS Cancelled
              FROM TeacherMeetings GROUP BY TeacherId
            )
            SELECT TeacherId AS teacherId,TeacherName AS name,Subject AS subject,
              CONVERT(varchar(10),COALESCE((SELECT MIN(TM.PTMDate) FROM TeacherMeetings TM WHERE TM.TeacherId=TS.TeacherId AND TM.PTMDate>=CAST(GETDATE() AS date) AND TM.MeetingStatus<>'Cancelled'),(SELECT MAX(TM.PTMDate) FROM TeacherMeetings TM WHERE TM.TeacherId=TS.TeacherId)),23) AS ptmDate,
              TotalMeetings AS total,Completed AS completed,Pending AS pending,Cancelled AS cancelled,
              CASE WHEN TotalMeetings=0 THEN 0 ELSE CONVERT(int,ROUND(Completed*100.0/TotalMeetings,0)) END AS completionRate
            FROM TeacherSummary TS ORDER BY TotalMeetings DESC,TeacherName ASC
            """, schoolId, schoolId, schoolId, session, session);
    }

    public List<Map<String, Object>> upcoming(String schoolId, String session) {
        return jdbc.queryForList("""
            SELECT DISTINCT ta.TeacherId AS teacherId,ISNULL(ta.TeacherName,'') AS teacher,
              ISNULL(ta.Subject,'') AS subject,m.PTMId AS ptmId,ISNULL(m.PTMName,'') AS ptmName,
              CONVERT(varchar(10),m.PTMDate,23) AS date,m.StartTime AS time,ISNULL(c.ClassName,'') AS cls,
              ISNULL(px.SectionName,'') AS sectionName
            FROM PTM_TeacherAssignment ta JOIN PTM_Master m ON m.PTMId=ta.PTMId AND m.SchoolId=?
            LEFT JOIN PTM_Class c ON c.PTMId=ta.PTMId AND c.ClassId=ta.ClassId
            LEFT JOIN PTM_Section px ON px.PTMId=ta.PTMId AND px.SecId=TRY_CONVERT(int,ta.SecId)
            WHERE m.PTMDate IS NOT NULL AND CAST(m.PTMDate AS date)>=CAST(GETDATE() AS date)
              AND m.Status IN('Completed','Published')
              AND (ISNULL(?,'')='' OR m.Session=?)
            ORDER BY [date], [time], [teacher]
            OFFSET 0 ROWS FETCH NEXT 30 ROWS ONLY
            """, schoolId, session, session);
    }

    public Map<String, Object> overview(String schoolId) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT COUNT(*) AS totalMeetings,
              COALESCE(SUM(CASE WHEN MeetingStatus='Completed' THEN 1 ELSE 0 END),0) AS totalCompleted,
              COALESCE(SUM(CASE WHEN MeetingStatus IN('Scheduled','Draft') THEN 1 ELSE 0 END),0) AS totalPending,
              COALESCE(SUM(CASE WHEN MeetingStatus='Cancelled' THEN 1 ELSE 0 END),0) AS totalCancelled
            FROM(SELECT DISTINCT ta.TeacherId,m.PTMId,m.Status AS MeetingStatus
                 FROM PTM_TeacherAssignment ta JOIN PTM_Master m ON m.PTMId=ta.PTMId
                 WHERE m.SchoolId=?)x
            """, schoolId);
        return rows.isEmpty() ? new LinkedHashMap<>() : rows.get(0);
    }

    private static Object blankNull(String value) { return value == null || value.trim().isEmpty() ? null : value.trim(); }
    private static String placeholders(Collection<?> values, List<Object> args) {
        if (values == null || values.isEmpty()) return "";
        List<String> out = new ArrayList<>(); for (Object value : values) { out.add("?"); args.add(value); }
        return String.join(",", out);
    }
}
