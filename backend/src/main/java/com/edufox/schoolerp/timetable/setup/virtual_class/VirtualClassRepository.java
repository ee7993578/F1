package com.edufox.schoolerp.timetable.setup.virtual_class;
import com.edufox.schoolerp.timetable.work.timetable.*;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

/** Direct-table implementation of Create_virtualclass and its binding procedures. */
@Repository
@RequiredArgsConstructor
public class VirtualClassRepository {
    private final JdbcTemplate jdbc;

    private int count(String sql, Object... args) { return Objects.requireNonNullElse(jdbc.queryForObject(sql, Integer.class, args), 0); }
    private static int number(Object value) { try { return Integer.parseInt(String.valueOf(value)); } catch (Exception e) { return 0; } }
    private static String text(Object value) { return value == null ? "" : value.toString().trim(); }
    private static void require(boolean valid, String message) { if (!valid) throw new IllegalArgumentException(message); }

    public String currentSession(String school) {
        return jdbc.queryForObject("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC", String.class, school);
    }

    private boolean administrator(String school, String staff) {
        // GetDesignationId translates these designations to 28.
        return count("SELECT COUNT(*) FROM desig_mapping WHERE school_id=? AND staff_u_id=? AND desg_id IN (28,31,34,70,39)", school, staff) > 0;
    }

    private int ownFaculty(String school, String staff) {
        var rows=jdbc.queryForList("SELECT F_id FROM TB_FacultyMaster WHERE school_id=? AND staff_u_id=? ORDER BY F_id", school, staff);
        return rows.isEmpty()?0:number(rows.getFirst().get("F_id"));
    }

    public int faculty(String school, String staff, int requested) {
        if (administrator(school,staff)) return requested;
        int own=ownFaculty(school,staff);
        require(own>0 && (requested==0 || requested==own), "You can manage only your own virtual classes");
        return own;
    }

    public Map<String,Object> options(String school,String staff) {
        String session=currentSession(school);
        boolean admin=administrator(school,staff);
        var result=new LinkedHashMap<String,Object>();
        result.put("session",session);
        result.put("canSelectFaculty",admin);
        result.put("facultyId",admin?0:ownFaculty(school,staff));
        result.put("faculties",jdbc.queryForList("""
            SELECT f.F_id AS id,UPPER(REPLACE(CONCAT(f.First_name,' ',f.Mid_name,' ',f.Last_name),'  ',' ')) AS label
            FROM TB_FacultyMaster f WHERE f.school_id=? AND EXISTS
            (SELECT 1 FROM tb_faculty_withdrawn w WHERE w.school_id=f.school_id AND w.f_id=f.F_id AND w.session=? AND w.status='Registered')
            ORDER BY label
            """,school,session));
        result.put("subjects",jdbc.queryForList("SELECT id,Subject AS label FROM TB_SubjectMaster WHERE school_id=? AND status=1 ORDER BY Subject",school));
        result.put("sections",jdbc.queryForList("""
            SELECT c.Sec_id AS id,cn.class_name+'('+c.Section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE c.school_id=? ORDER BY c.class_id,c.Sec_id
            """,school));
        return result;
    }

    public List<Map<String,Object>> candidates(String school,String session,int section,int subject) {
        // bind_infosheet1: registered students NOT already assigned for this section/subject.
        return jdbc.queryForList("""
            SELECT sm.stu_id AS id,sm.sec_id AS sectionId,so.registration_no AS admissionNo,
              LTRIM(RTRIM(CONCAT(so.first_name,' ',so.mid_name,' ',so.last_name))) AS name,
              cn.class_name+'('+c.Section+')' AS sectionLabel
            FROM student_other_info so JOIN TB_StudentMaster sm
              ON so.uid=sm.uid AND so.Registration_NO=sm.Registration_NO AND so.school_id=sm.school_id
            JOIN TB_Class c ON c.school_id=sm.school_id AND c.Sec_id=sm.sec_id
            JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE sm.school_id=? AND sm.session=? AND sm.status='registered' AND sm.sec_id=?
              AND NOT EXISTS (SELECT 1 FROM TB_VirtualClass v WHERE v.school_id=sm.school_id
                AND v.session=sm.session AND v.sec_id=sm.sec_id AND v.subject_id=? AND v.stu_id=sm.stu_id)
            ORDER BY so.first_name,so.mid_name,so.last_name,sm.stu_id
            """,school,session,section,subject);
    }

    public Map<String,Object> read(String school,String staff,String session,Map<String,Object> filter) {
        int fid=faculty(school,staff,number(filter.get("facultyId"))),id=number(filter.get("vClassId"));
        var rows=jdbc.queryForList("""
            SELECT v.VClass_id AS vClassId,v.V_Class AS vClassName,v.subject_id AS subjectId,s.Subject AS subjectName,
              v.f_id AS facultyId,COUNT(*) AS studentCount
            FROM TB_VirtualClass v JOIN TB_SubjectMaster s ON s.school_id=v.school_id AND s.id=v.subject_id
            WHERE v.school_id=? AND v.session=? AND (?=0 OR v.f_id=?) AND (?=0 OR v.VClass_id=?)
            GROUP BY v.VClass_id,v.V_Class,v.subject_id,s.Subject,v.f_id ORDER BY v.V_Class
            """,school,session,fid,fid,id,id);
        // Don't accidentally return every student's membership when just listing classes.
        List<Map<String,Object>> students=List.of();
        if(id>0) {
            require(!rows.isEmpty(),"Virtual class was not found for the selected faculty");
            int subject=number(filter.get("subjectId"));
            students=members(school,session,id).stream().filter(row -> subject==0 || number(row.get("subjectId"))==subject).toList();
        }
        return Map.of("rows",rows,"students",students);
    }

    public List<Map<String,Object>> members(String school,String session,int id) {
        // GetVirtualClassStudentList includes withdrawn members, and locks anyone with senior_marks.
        return jdbc.queryForList("""
            SELECT DISTINCT v.VClass_id AS vClassId,v.stu_id AS id,v.sec_id AS sectionId,v.subject_id AS subjectId,
              v.f_id AS facultyId,sm.Registration_NO AS admissionNo,
              LTRIM(RTRIM(CONCAT(so.first_name,' ',so.mid_name,' ',so.last_name))) AS name,
              cn.class_name+'('+c.Section+')' AS sectionLabel,
              CASE WHEN EXISTS(SELECT 1 FROM senior_marks m WHERE m.school_id=v.school_id AND m.session=v.session
                AND m.stu_id=v.stu_id AND m.subject_id=v.subject_id) THEN 1 ELSE 0 END AS locked
            FROM TB_VirtualClass v JOIN TB_StudentMaster sm
              ON sm.school_id=v.school_id AND sm.session=v.session AND sm.stu_id=v.stu_id
            JOIN student_other_info so ON so.school_id=sm.school_id AND so.uid=sm.uid AND so.Registration_NO=sm.Registration_NO
            JOIN TB_Class c ON c.school_id=v.school_id AND c.Sec_id=v.sec_id
            JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE v.school_id=? AND v.session=? AND v.VClass_id=? ORDER BY name
            """,school,session,id);
    }

    public int save(String school,String staff,Map<String,Object> body) {
        String session=currentSession(school),name=text(body.get("vClassName")).replace("'", "");
        int fid=faculty(school,staff,number(body.get("facultyId"))),subject=number(body.get("subjectId"));
        int section=number(body.get("sectionId")),id=number(body.get("vClassId"));
        require(fid>0 && subject>0 && !name.isBlank() && name.length()<=50,"Select faculty, subject and a virtual class name (maximum 50 characters)");
        require(count("SELECT COUNT(*) FROM TB_SubjectMaster WHERE school_id=? AND id=? AND status=1",school,subject)>0,"Please select a valid subject");
        var facultyRows=jdbc.queryForList("SELECT staff_u_id FROM TB_FacultyMaster WHERE school_id=? AND F_id=?",school,fid);
        require(!facultyRows.isEmpty(),"Please select a valid faculty");
        Object facultyStaff=facultyRows.getFirst().get("staff_u_id");
        Set<Integer> selected=new LinkedHashSet<>();
        if(body.get("studentIds") instanceof List<?> ids) for(Object value:ids) { int student=number(value); require(student>0,"Invalid student"); selected.add(student); }
        require(id>0 || !selected.isEmpty(),"Please add students to Selected Student");
        List<Map<String,Object>> existing=List.of();
        if(id>0) {
            var owner=jdbc.queryForList("SELECT DISTINCT f_id,subject_id FROM TB_VirtualClass WITH (UPDLOCK,HOLDLOCK) WHERE school_id=? AND session=? AND VClass_id=?",school,session,id);
            require(!owner.isEmpty(),"Virtual class was not found");
            for(var row:owner) require(number(row.get("f_id"))==fid,"Virtual class does not belong to the selected faculty");
            existing=members(school,session,id).stream().filter(row -> number(row.get("subjectId"))==subject).toList();
            for(var row:existing) require(number(row.get("locked"))==0 || selected.contains(number(row.get("id"))),"Marks already defined: this student cannot be removed");
        } else {
            require(count("SELECT COUNT(*) FROM TB_ClassSubject cs WHERE cs.school_id=? AND cs.session=? AND cs.subject_id=? AND (cs.class_id=? OR cs.class_id=(SELECT class_id FROM TB_Class WHERE school_id=? AND Sec_id=?))",school,session,subject,section,school,section)>0,"Please define class subject.");
            id=count("SELECT ISNULL(MAX(VClass_id),1000)+1 FROM TB_VirtualClass WITH (UPDLOCK,HOLDLOCK) WHERE school_id=?",school);
        }
        Set<Integer> oldIds=new HashSet<>(); for(var row:existing) oldIds.add(number(row.get("id")));
        Map<Integer,Integer> sections=new LinkedHashMap<>();
        for(int student:selected) {
            var studentRows=jdbc.queryForList("SELECT sec_id,status FROM TB_StudentMaster WHERE school_id=? AND session=? AND stu_id=?",school,session,student);
            require(studentRows.size()==1,"Student was not found in the current school session");
            require(oldIds.contains(student)||"registered".equalsIgnoreCase(text(studentRows.getFirst().get("status"))),"Only registered students can be added");
            int studentSection=number(studentRows.getFirst().get("sec_id"));
            require(count("SELECT COUNT(*) FROM TB_VirtualClass WITH (UPDLOCK,HOLDLOCK) WHERE school_id=? AND session=? AND stu_id=? AND subject_id=? AND VClass_id<>?",school,session,student,subject,id)==0,"Student already belongs to a virtual class for this subject");
            sections.put(student,studentSection);
        }
        for(int student:oldIds) if(!selected.contains(student)) jdbc.update("DELETE FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=? AND subject_id=? AND stu_id=?",school,session,id,subject,student);
        for(var student:sections.entrySet()) {
            if(oldIds.contains(student.getKey())) {
                jdbc.update("UPDATE TB_VirtualClass SET V_Class=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND session=? AND VClass_id=? AND subject_id=? AND stu_id=?",name,staff,school,session,id,subject,student.getKey());
            } else {
                jdbc.update("""
                    INSERT INTO TB_VirtualClass(V_Class,VClass_id,Stu_id,subject_id,sec_id,f_id,staff_u_id,
                      created_date,created_by,modified_date,modified_by,school_id,session)
                    VALUES(?,?,?,?,?,?,?,GETDATE(),?,GETDATE(),?,?,?)
                    """,name,id,student.getKey(),subject,student.getValue(),fid,facultyStaff,staff,staff,school,session);
            }
        }
        if(!selected.isEmpty() && count("SELECT COUNT(*) FROM faculty_subject_mapping WHERE school_id=? AND session=? AND sec_id=? AND f_id=? AND sub_id=?",school,session,id,fid,subject)==0)
            jdbc.update("INSERT INTO faculty_subject_mapping(f_id,staff_u_id,sec_id,sub_id,created_date,created_by,school_id,session) VALUES(?,?,?,?,GETDATE(),?,?,?)",fid,facultyStaff,id,subject,staff,school,session);
        return selected.size();
    }

    public int delete(String school,String staff,Map<String,Object> body) {
        // The legacy page always operates on the server's current session; never
        // trust a client-supplied session when deleting a class.
        String session=currentSession(school); int id=number(body.get("vClassId"));
        var owners=jdbc.queryForList("SELECT DISTINCT f_id FROM TB_VirtualClass WITH (UPDLOCK,HOLDLOCK) WHERE school_id=? AND session=? AND VClass_id=?",school,session,id);
        require(!owners.isEmpty(),"Virtual class was not found");
        for(var row:owners) faculty(school,staff,number(row.get("f_id")));
        require(count("""
            SELECT COUNT(*) FROM senior_marks m WHERE m.school_id=? AND m.session=?
            AND m.stu_id IN (SELECT stu_id FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=?)
            AND m.subject_id IN (SELECT subject_id FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=?)
            """,school,session,school,session,id,school,session,id)==0,"Marks already defined for this class");
        int deleted=jdbc.update("DELETE FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=?",school,session,id);
        jdbc.update("DELETE FROM faculty_subject_mapping WHERE school_id=? AND session=? AND sec_id=?",school,session,id);
        return deleted;
    }
}
