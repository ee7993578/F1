package com.edufox.schoolerp.timetable.work.timetable;
import com.edufox.schoolerp.timetable.setup.virtual_class.*;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class TimetableRepository {
    private final JdbcTemplate jdbc;
    private static final List<String> DAYS=List.of("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");

    public Map<String,Object> bootstrap(String school, String requested) {
        String session = requested == null || requested.isBlank() ? defaultSession(school) : requested;
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("session", session);
        result.put("sessions", safe("SELECT DISTINCT session AS id,session AS label FROM TB_Session WHERE school_id=? ORDER BY session DESC", school));
        result.put("configs", configs(school, session));
        result.put("sections", sections(school, session));
        result.put("classes", safe("SELECT id,class_name AS label,class_name AS name FROM class_name WHERE school_id=? ORDER BY COALESCE(class_order,9999),class_name", school));
        result.put("faculties", faculties(school, session));
        result.put("subjects", safe("SELECT id,Subject AS label,Subject AS name FROM TB_SubjectMaster WHERE school_id=? AND status=1 ORDER BY Subject", school));
        result.put("days", safe("SELECT weekdays AS id,weekdays AS label FROM tb_days ORDER BY id"));
        result.put("activities", safe("SELECT activity_id AS id,activity_name AS name,activity_date AS date FROM Activity WHERE school_id=? AND session=? ORDER BY activity_date,activity_name", school, session));
        result.put("holidays", holidays(school, session).get("rows"));
        return result;
    }

    public List<Map<String,Object>> configs(String school, String session) {
        List<Map<String,Object>> result=safe("SELECT DISTINCT TT_Config AS id,TT_Config AS label,TT_UID AS uid,TT_UID AS ttUid FROM TB_Timetables_config WHERE school_id=? AND session=? AND Status='Active' ORDER BY TT_Config", school, session);
        for(Map<String,Object> config:result) config.put("periods",safe("SELECT lecture_id AS id,lecture_id AS period,lecture_name AS label,ST_Time_Hr+':'+ST_Time_Min+' '+ST_Time_Mode+' - '+END_Time_Hr+':'+END_Time_Min+' '+END_Time_Mode AS time,ST_Time_Hr AS startHour,ST_Time_Min AS startMinute,ST_Time_Mode AS startMode,Duration_Time_Min AS duration,END_Time_Hr AS endHour,END_Time_Min AS endMinute,END_Time_Mode AS endMode,Break_Time AS breakTime FROM TB_Timetables_config WHERE school_id=? AND session=? AND TT_UID=? AND Status='Active' ORDER BY TRY_CONVERT(int,lecture_id)",school,session,text(config.get("uid"))));
        return result;
    }

    private List<Map<String,Object>> faculties(String school, String session) {
        return safe("""
            SELECT DISTINCT f.F_id AS id,UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS label,
              f.staff_u_id AS staffUid
            FROM TB_FacultyMaster f
            JOIN tb_faculty_withdrawn w ON w.f_id=f.F_id AND w.school_id=f.school_id AND w.session=? AND w.status='Registered'
            WHERE f.school_id=? ORDER BY label
            """, session, school);
    }

    private List<Map<String,Object>> sections(String school, String session) {
        return safe("""
            SELECT c.Sec_id AS id,c.Sec_id AS sectionId,c.class_id AS classId,cn.class_name AS className,c.Section AS section,
                   cn.class_name+' ('+c.Section+')' AS label,'regular' AS type
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE c.school_id=?
            UNION
            SELECT v.VClass_id AS id,v.VClass_id AS sectionId,c.class_id AS classId,cn.class_name AS className,
                   v.V_Class AS section,v.V_Class AS label,'virtual' AS type
            FROM TB_VirtualClass v
            LEFT JOIN TB_Class c ON c.school_id=v.school_id AND c.Sec_id=v.sec_id
            LEFT JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE v.school_id=? AND v.session=?
            ORDER BY classId,id
            """, school, school, session);
    }

    public List<Map<String,Object>> assignment(String school, String session, int section, int faculty, String day, String ttUid) {
        String scope = faculty > 0 ? "t.f_id=?" : "t.sec_id=?";
        Object[] args = faculty > 0 ? new Object[]{school, session, faculty, day, ttUid} : new Object[]{school, session, section, day, ttUid};
        String sql="SELECT t.id,t.TDay AS day,t.Period AS period,t.sec_id AS sectionId,t.F_id AS facultyId,t.Subject_id AS subjectId,COALESCE(sm.Subject,'') AS subjectName,UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS facultyName,c.lecture_name AS lectureName,c.ST_Time_Hr+':'+c.ST_Time_Min+' '+c.ST_Time_Mode+' - '+c.END_Time_Hr+':'+c.END_Time_Min+' '+c.END_Time_Mode AS time FROM TB_TimeTable t LEFT JOIN TB_SubjectMaster sm ON sm.id=t.Subject_id AND sm.school_id=t.school_id LEFT JOIN TB_FacultyMaster f ON f.F_id=t.F_id AND f.school_id=t.school_id LEFT JOIN TB_Timetables_config c ON c.school_id=t.school_id AND c.session=t.session AND c.TT_UID=t.TT_UID AND c.lecture_id=t.Period WHERE t.school_id=? AND t.session=? AND "+scope+" AND t.TDay=? AND t.TT_UID=? ORDER BY TRY_CONVERT(int,t.Period),t.id";
        return safe(sql, args);
    }

    public int saveAssignment(String school, String staff, Map<String,Object> body) {
        String session=text(body.get("session")), day=text(body.get("day")), uid=text(body.get("ttUid"));
        String mode=text(body.get("mode")).toLowerCase(Locale.ROOT);
        int section=integer(body.get("sectionId")), faculty=integer(body.get("facultyId"));
        List<String> days=new ArrayList<>();
        for(Object d:list(body.get("days"))) if(!text(d).isBlank()) days.add(text(d));
        if(days.isEmpty() && !day.isBlank()) days.add(day);
        if(days.isEmpty()) throw new IllegalArgumentException("Please select a day");
        if(uid.isBlank()) throw new IllegalArgumentException("Please select a time table");

        int count=0;
        if("faculty".equals(mode)) {
            if(faculty<=0) throw new IllegalArgumentException("Please select a faculty");
            for(String selectedDay:days) {
                jdbc.update("DELETE FROM TB_TimeTable WHERE school_id=? AND session=? AND f_id=? AND TDay=? AND TT_UID=?", school,session,faculty,selectedDay,uid);
                for(Map<String,Object> row: rows(body)) {
                    int subject=integer(row.get("subjectId")), targetSection=integer(row.get("sectionId"));
                    String period=text(row.get("period"));
                    if(subject<=0 || targetSection<=0 || period.isBlank()) continue;
                    int nextTtId=scalar("SELECT COALESCE(MAX(TT_ID),0)+1 FROM TB_TimeTable WHERE school_id=? AND session=?",school,session);
                    jdbc.update("INSERT INTO TB_TimeTable(F_id,TDay,Period,sec_id,Subject_id,TT_ID,TT_UID,created_by,modified_date,modified_by,school_id,session) VALUES(?,?,?,?,?,?,?, ?,GETDATE(),?,?,?)", faculty,selectedDay,period,targetSection,subject,nextTtId,uid,staff,staff,school,session);
                    count++;
                }
            }
            return count;
        }

        if(section<=0) throw new IllegalArgumentException("Please select a class / section");
        for(String selectedDay:days) {
            jdbc.update("DELETE FROM TB_TimeTable WHERE school_id=? AND session=? AND sec_id=? AND TDay=? AND TT_UID=?", school,session,section,selectedDay,uid);
            for(Map<String,Object> row: rows(body)) {
                int subject=integer(row.get("subjectId")), rowFaculty=integer(row.get("facultyId"));
                String period=text(row.get("period"));
                if(subject<=0 || rowFaculty<=0 || period.isBlank()) continue;
                int occupied=scalar("SELECT COUNT(*) FROM TB_TimeTable WHERE school_id=? AND session=? AND TT_UID=? AND TDay=? AND Period=? AND F_id=? AND sec_id<>?",school,session,uid,selectedDay,period,rowFaculty,section);
                if(occupied>0) throw new IllegalArgumentException("Faculty is already assigned for another class in " + selectedDay + " period " + period);
                int nextTtId=scalar("SELECT COALESCE(MAX(TT_ID),0)+1 FROM TB_TimeTable WHERE school_id=? AND session=?",school,session);
                jdbc.update("INSERT INTO TB_TimeTable(F_id,TDay,Period,sec_id,Subject_id,TT_ID,TT_UID,created_by,modified_date,modified_by,school_id,session) VALUES(?,?,?,?,?,?,?, ?,GETDATE(),?,?,?)", rowFaculty,selectedDay,period,section,subject,nextTtId,uid,staff,staff,school,session);
                count++;
            }
        }
        return count;
    }

    public Map<String,Object> alternate(String school,String session,int faculty,String ttUid) {
        String day=LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.ENGLISH);
        List<Map<String,Object>> rows=safe("""
            SELECT t.sec_id AS sectionId,t.Period AS period,c.lecture_name AS lectureName,
              c.ST_Time_Hr+':'+c.ST_Time_Min+' '+c.ST_Time_Mode+' - '+c.END_Time_Hr+':'+c.END_Time_Min+' '+c.END_Time_Mode AS time,
              cn.class_name+' ('+cl.Section+')' AS className,
              COALESCE(a.alt_fid,0) AS alternateFacultyId
            FROM TB_TimeTable t JOIN TB_Timetables_config c ON c.school_id=t.school_id AND c.session=t.session AND c.TT_UID=t.TT_UID AND c.lecture_id=t.Period
            JOIN TB_Class cl ON cl.school_id=t.school_id AND cl.Sec_id=t.sec_id JOIN class_name cn ON cn.school_id=cl.school_id AND cn.id=cl.class_id
            LEFT JOIN alt_timetable a ON a.school_id=t.school_id AND a.session=t.session AND a.f_id=t.F_id AND a.tday=t.TDay AND a.period=TRY_CONVERT(int,t.Period) AND a.sec_id=t.sec_id AND a.status=1
            WHERE t.school_id=? AND t.session=? AND t.f_id=? AND t.TDay=? AND t.TT_UID=? ORDER BY TRY_CONVERT(int,t.Period)
            """,school,session,faculty,day,ttUid);
        for(Map<String,Object> row:rows) row.put("availableFaculties",safe("SELECT DISTINCT f.F_id AS id,UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS label FROM TB_FacultyMaster f JOIN tb_faculty_withdrawn w ON w.f_id=f.F_id AND w.school_id=f.school_id AND w.session=? AND w.status='Registered' WHERE f.school_id=? AND f.F_id<>? AND NOT EXISTS(SELECT 1 FROM TB_TimeTable t WHERE t.school_id=f.school_id AND t.session=? AND t.F_id=f.F_id AND t.TDay=? AND t.Period=? AND t.TT_UID=?) AND NOT EXISTS(SELECT 1 FROM alt_timetable a WHERE a.school_id=f.school_id AND a.session=? AND a.alt_fid=f.F_id AND a.tday=? AND a.period=? AND a.status=1) ORDER BY label",session,school,faculty,session,day,text(row.get("period")),ttUid,session,day,text(row.get("period"))));
        Map<String,Object> r=new LinkedHashMap<>();r.put("day",day);r.put("rows",rows);r.put("faculties",faculties(school,session));return r;
    }

    public int saveAlternate(String school,String staff,Map<String,Object> body) {
        String session=text(body.get("session")),day=text(body.get("day")); int faculty=integer(body.get("facultyId"));
        if(day.isBlank()) day=LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL,Locale.ENGLISH);
        jdbc.update("UPDATE alt_timetable SET status=0 WHERE school_id=? AND session=? AND f_id=? AND tday=?",school,session,faculty,day);
        int n=0; for(Map<String,Object> row:rows(body)){int alt=integer(row.get("alternateFacultyId")); if(alt<=0)continue; jdbc.update("INSERT INTO alt_timetable(f_id,alt_fid,tday,period,sec_id,created_date,created_by,school_id,session,status) VALUES(?,?,?, ?,?,GETDATE(),?,?,?,1)",faculty,alt,day,integer(row.get("period")),integer(row.get("sectionId")),staff,school,session);n++;} return n;
    }

    public Map<String,Object> activity(String school,String session,int classId){
        List<Map<String,Object>> acts=safe("SELECT activity_id AS id,activity_name AS name,activity_date AS date,CASE WHEN EXISTS(SELECT 1 FROM assign_activity aa WHERE aa.activity_id=a.activity_id AND aa.class_id=? AND aa.school_id=? AND aa.session=? AND aa.status=1) THEN 1 ELSE 0 END AS selected FROM Activity a WHERE a.school_id=? AND a.session=? ORDER BY a.activity_date,a.activity_name",classId,school,session,school,session);
        Map<String,Object> r=new LinkedHashMap<>();r.put("classId",classId);r.put("activities",acts);r.put("classes",safe("SELECT id,class_name AS label FROM class_name WHERE school_id=? ORDER BY COALESCE(class_order,9999),class_name",school));return r;
    }
    public int saveActivity(String school,String staff,Map<String,Object> body){String session=text(body.get("session"));int classId=integer(body.get("classId"));jdbc.update("UPDATE assign_activity SET status=0,modified_by=?,modified_date=GETDATE() WHERE class_id=? AND school_id=? AND session=?",staff,classId,school,session);int n=0;for(Object x:list(body.get("activityIds"))){int id=integer(x);if(id>0){jdbc.update("INSERT INTO assign_activity(class_id,activity_id,school_id,session,created_by,created_date,modified_by,modified_date,status) VALUES(?,?,?, ?,?,GETDATE(),?,GETDATE(),1)",classId,id,school,session,staff,staff);n++;}}return n;}

    public Map<String,Object> holidays(String school,String session){Map<String,Object> r=new LinkedHashMap<>();r.put("rows",safe("SELECT h.Holiday_name AS holidayName,h.fromdate AS fromDate,h.todate AS toDate,STRING_AGG(cn.class_name,', ') AS classes FROM define_holidays h LEFT JOIN class_name cn ON cn.school_id=h.school_id AND cn.id=h.class_id WHERE h.school_id=? AND h.session=? GROUP BY h.Holiday_name,h.fromdate,h.todate ORDER BY h.fromdate",school,session));r.put("classes",safe("SELECT id,class_name AS label FROM class_name WHERE school_id=? ORDER BY COALESCE(class_order,9999),class_name",school));return r;}
    public int saveHoliday(String school,String staff,Map<String,Object> body){String session=text(body.get("session")),name=text(body.get("name")).trim().toUpperCase(),from=legacyDate(text(body.get("fromDate"))),to=legacyDate(text(body.get("toDate")));int n=0;for(Object x:list(body.get("classIds"))){int c=integer(x);if(c>0&&scalar("SELECT COUNT(*) FROM define_holidays WHERE Holiday_name=? AND class_id=? AND school_id=? AND session=?",name,c,school,session)==0){jdbc.update("INSERT INTO define_holidays(Holiday_name,fromdate,todate,class_id,school_id,session,created_by,created_date) VALUES(?,?,?,?,?,?,?,CONVERT(varchar(50),GETDATE(),106))",name,from,to,c,school,session,staff);n++;}}return n;}
    public int deleteHoliday(String school,String staff,Map<String,Object> body){return jdbc.update("DELETE FROM define_holidays WHERE school_id=? AND session=? AND Holiday_name=? AND fromdate=? AND todate=?",school,text(body.get("session")),text(body.get("name")),text(body.get("fromDate")),text(body.get("toDate")));}

    public Map<String,Object> mapping(String school,String session,int faculty,int section){Map<String,Object> r=new LinkedHashMap<>();r.put("subjects",subjectsForSection(school,session,section));r.put("selected",safe("SELECT sub_id AS subjectId FROM faculty_subject_mapping WHERE school_id=? AND session=? AND f_id=? AND sec_id=?",school,session,faculty,section));return r;}
    public int saveMapping(String school,String staff,Map<String,Object> body){String session=text(body.get("session"));int faculty=integer(body.get("facultyId")),section=integer(body.get("sectionId"));jdbc.update("DELETE FROM faculty_subject_mapping WHERE school_id=? AND session=? AND f_id=? AND sec_id=?",school,session,faculty,section);int n=0;for(Object x:list(body.get("subjectIds"))){int sub=integer(x);if(sub>0){jdbc.update("INSERT INTO faculty_subject_mapping(f_id,staff_u_id,sec_id,sub_id,school_id,session,created_by) VALUES(?,?,?,?,?,?,?)",faculty,staff,section,sub,school,session,staff);n++;}}return n;}
    public List<Map<String,Object>> subjects(String school,String session,int section){return subjectsForSection(school,session,section);}
    private List<Map<String,Object>> subjectsForSection(String school,String session,int section){int real=scalar("SELECT TOP 1 sec_id FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=?",school,session,section);if(real<=0)real=section;int classId=scalar("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND Sec_id=?",school,real);return safe("SELECT DISTINCT cs.subject_id AS id,sm.Subject AS label FROM TB_ClassSubject cs JOIN TB_SubjectMaster sm ON sm.school_id=cs.school_id AND sm.id=cs.subject_id WHERE cs.school_id=? AND cs.session=? AND cs.status=1 AND cs.class_id IN (?,?) ORDER BY sm.Subject",school,session,classId,real);}

    public Map<String,Object> virtualClass(String school,String session,Map<String,Object> filter){String f=text(filter.get("facultyId")),s=text(filter.get("subjectId"));String where=" WHERE v.school_id=? AND v.session=? ";List<Object> args=new ArrayList<>(List.of(school,session));if(!f.isBlank()&&!f.equals("0")){where+=" AND v.f_id=?";args.add(f);}if(!s.isBlank()&&!s.equals("0")){where+=" AND v.subject_id=?";args.add(s);}Map<String,Object> r=new LinkedHashMap<>();r.put("rows",safe("SELECT v.VClass_id AS vClassId,v.V_Class AS vClassName,v.subject_id AS subjectId,sm.Subject AS subjectName,v.f_id AS facultyId,UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS facultyName,v.sec_id AS sectionId,COUNT(*) AS studentCount FROM TB_VirtualClass v LEFT JOIN TB_SubjectMaster sm ON sm.school_id=v.school_id AND sm.id=v.subject_id LEFT JOIN TB_FacultyMaster f ON f.school_id=v.school_id AND f.F_id=v.f_id "+where+" GROUP BY v.VClass_id,v.V_Class,v.subject_id,sm.Subject,v.f_id,f.First_name,f.Mid_name,f.Last_name,v.sec_id ORDER BY v.V_Class",args.toArray()));r.put("students",safe("SELECT v.VClass_id AS vClassId,s.stu_id AS id,s.Registration_NO AS admissionNo,LTRIM(RTRIM(COALESCE(i.First_name,'')+' '+COALESCE(i.Mid_name,'')+' '+COALESCE(i.Last_name,''))) AS name,s.sec_id AS sectionId FROM TB_VirtualClass v JOIN TB_StudentMaster s ON s.school_id=v.school_id AND s.session=v.session AND s.stu_id=v.Stu_id LEFT JOIN student_other_info i ON i.school_id=s.school_id AND i.Registration_NO=s.Registration_NO WHERE v.school_id=? AND v.session=? AND LOWER(s.status)='registered' ORDER BY name",school,session));return r;}
    public int saveVirtualClass(String school,String staff,Map<String,Object> body){String session=text(body.get("session")),name=text(body.get("vClassName")).trim();int id=integer(body.get("vClassId"));int subject=integer(body.get("subjectId")),section=integer(body.get("sectionId")),faculty=integer(body.get("facultyId"));if(name.isBlank()||subject<=0||section<=0||faculty<=0)throw new IllegalArgumentException("Faculty, subject, class and virtual class name are required");int classId=scalar("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND Sec_id=?",school,section);if(scalar("SELECT COUNT(*) FROM TB_ClassSubject WHERE school_id=? AND session=? AND subject_id=? AND class_id IN (?,?) AND status=1",school,session,subject,classId,section)==0)throw new IllegalArgumentException("Please define class subject first");if(id<=0)id=scalar("SELECT COALESCE(MAX(VClass_id),999)+1 FROM TB_VirtualClass WHERE school_id=?",school);else jdbc.update("DELETE FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=?",school,session,id);int n=0;for(Object x:list(body.get("studentIds"))){int stu=integer(x);if(stu<=0)continue;if(scalar("SELECT COUNT(*) FROM TB_StudentMaster WHERE school_id=? AND session=? AND stu_id=? AND sec_id=? AND LOWER(status)='registered'",school,session,stu,section)==0)continue;if(scalar("SELECT COUNT(*) FROM TB_VirtualClass WHERE school_id=? AND session=? AND subject_id=? AND sec_id=? AND Stu_id=? AND f_id<>?",school,session,subject,section,stu,faculty)>0)throw new IllegalArgumentException("One or more selected students already belongs to another virtual class for this subject");jdbc.update("INSERT INTO TB_VirtualClass(V_Class,VClass_id,Stu_id,subject_id,sec_id,f_id,staff_u_id,created_date,created_by,modified_date,modified_by,school_id,session) VALUES(?,?,?,?,?,?,?,GETDATE(),?,'','',?,?)",name,id,stu,subject,section,faculty,staff,staff,school,session);n++;}if(n==0)throw new IllegalArgumentException("Please select at least one student");return n;}
    public int deleteVirtualClass(String school,String staff,Map<String,Object> body){return jdbc.update("DELETE FROM TB_VirtualClass WHERE school_id=? AND session=? AND VClass_id=?",school,text(body.get("session")),integer(body.get("vClassId")));}

    public int saveConfig(String school,String staff,Map<String,Object> body){String session=text(body.get("session")),name=text(body.get("configName")).trim().toUpperCase(),uid=text(body.get("ttUid"));if(uid.isBlank())uid=UUID.randomUUID().toString().replace("-","").substring(0,5);if(scalar("SELECT COUNT(*) FROM TB_Timetables_config WHERE school_id=? AND session=? AND TT_Config=? AND TT_UID<>?",school,session,name,uid)>0)throw new IllegalArgumentException("Time table configuration already exists");if(integer(body.get("id"))!=0){jdbc.update("DELETE FROM TB_Timetables_config WHERE school_id=? AND session=? AND TT_UID=?",school,session,uid);jdbc.update("DELETE FROM TB_Period_Time WHERE school_id=? AND session=? AND tt_uid=?",school,session,uid);}int n=0;for(Map<String,Object> p:rows(body)){String period=text(p.get("period"));if(period.isBlank())period="Period"+(n+1);String num=period.replaceAll("[^0-9]","");if(num.isBlank())num=String.valueOf(n+1);String lecture="PERIOD"+num;String sh=text(p.get("startHour")),sm=text(p.get("startMinute")),smod=text(p.get("startMode")),eh=text(p.get("endHour")),em=text(p.get("endMinute")),emode=text(p.get("endMode")),duration=text(p.get("duration")),br=text(p.get("breakTime"));int ttId=scalar("SELECT COALESCE(MAX(TT_ID),0)+1 FROM TB_Timetables_config WHERE school_id=?",school);jdbc.update("INSERT INTO TB_Timetables_config(session,TT_Config,lecture_id,lecture_name,tt_uid,TT_Duration_Nm,ST_Time_Hr,ST_Time_Min,ST_Time_Mode,Duration_Time_Min,END_Time_Hr,END_Time_Min,END_Time_Mode,Break_Time,Status,Created_by,school_id,TT_ID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",session,name,num,lecture,uid,duration,sh,sm,smod,duration,eh,em,emode,br,"Active",staff,school,ttId);jdbc.update("INSERT INTO TB_Period_Time(Period,Start_time,End_time,tt_uid,school_id,session) VALUES(?,?,?,?,?,?)",period,sh+":"+sm,eh+":"+em,uid,school,session);n++;}return n;}
    public int deleteConfig(String school,String staff,Map<String,Object> body){String session=text(body.get("session")),uid=text(body.get("ttUid"));int n=jdbc.update("DELETE FROM TB_Timetables_config WHERE school_id=? AND session=? AND TT_UID=?",school,session,uid);jdbc.update("DELETE FROM TB_Period_Time WHERE school_id=? AND session=? AND tt_uid=?",school,session,uid);return n;}

    public Map<String,Object> report(String school,String session,String type,Map<String,String> p){return switch(type==null?"timetable":type.toLowerCase(Locale.ROOT)){case "activity"->activityReport(school,session,p);case "arrangement"->arrangementReport(school,session,p);case "free"->freeReport(school,session);case "faculty"->facultyReport(school,session,p);case "school"->schoolReport(school,session,p);case "virtual"->virtualReport(school,session,p);default->timeReport(school,session,p);};}
    private Map<String,Object> activityReport(String school,String session,Map<String,String> p){int classId=intValue(p.get("classId"));List<Map<String,Object>> rows=safe("SELECT cn.class_name AS className,aa.class_id AS classId,a.activity_date AS activityDate,a.activity_name AS activityName FROM assign_activity aa JOIN Activity a ON a.activity_id=TRY_CONVERT(int,aa.activity_id) AND a.school_id=aa.school_id JOIN class_name cn ON cn.id=aa.class_id AND cn.school_id=aa.school_id WHERE aa.school_id=? AND aa.session=? AND aa.status=1 "+(classId>0?"AND aa.class_id=?":"")+" ORDER BY cn.class_name,a.activity_date",classId>0?new Object[]{school,session,classId}:new Object[]{school,session});return result(List.of(col("className","Class"),col("activityDate","Activity Date"),col("activityName","Activity Name")),rows);}
    private Map<String,Object> arrangementReport(String school,String session,Map<String,String> p){String uid=value(p,"ttUid");List<Map<String,Object>> rows=safe("SELECT UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS faculty,c.lecture_name AS period,cn.class_name+' ('+cl.Section+')' AS className,UPPER(COALESCE(sm.Subject,'')) AS subject,a.alt_fid AS alternateFacultyId FROM alt_timetable a JOIN TB_Timetables_config c ON c.school_id=a.school_id AND c.session=a.session AND c.TT_UID=? AND c.lecture_id=CONVERT(varchar(50),a.period) JOIN TB_Class cl ON cl.school_id=a.school_id AND cl.Sec_id=a.sec_id JOIN class_name cn ON cn.school_id=cl.school_id AND cn.id=cl.class_id LEFT JOIN TB_SubjectMaster sm ON sm.school_id=a.school_id AND sm.id=(SELECT TOP 1 Subject_id FROM TB_TimeTable t WHERE t.school_id=a.school_id AND t.session=a.session AND t.sec_id=a.sec_id AND t.Period=CONVERT(varchar(50),a.period)) LEFT JOIN TB_FacultyMaster f ON f.school_id=a.school_id AND f.F_id=a.f_id WHERE a.school_id=? AND a.session=? AND a.status=1 ORDER BY faculty,TRY_CONVERT(int,c.lecture_id)",uid,school,session);return result(List.of(col("faculty","Faculty"),col("period","Period"),col("className","Class"),col("subject","Subject")),rows);}
    private Map<String,Object> freeReport(String school,String session){List<Map<String,Object>> rows=safe("SELECT UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS faculty,t.TDay AS day,STRING_AGG(CONVERT(varchar(20),c.lecture_id),', ') AS freePeriods FROM TB_FacultyMaster f JOIN (SELECT DISTINCT F_id,TDay FROM TB_TimeTable WHERE school_id=? AND session=? AND TDay<>'0') t ON t.F_id=f.F_id OUTER APPLY (SELECT lecture_id FROM TB_Timetables_config c WHERE c.school_id=? AND c.session=? AND c.TT_UID=(SELECT TOP 1 TT_UID FROM TB_TimeTable WHERE school_id=? AND session=?)) c WHERE f.school_id=? AND NOT EXISTS(SELECT 1 FROM TB_TimeTable x WHERE x.school_id=? AND x.session=? AND x.F_id=t.F_id AND x.TDay=t.TDay AND x.Period=c.lecture_id) GROUP BY f.First_name,f.Mid_name,f.Last_name,t.TDay ORDER BY faculty,t.TDay",school,session,school,session,school,session,school,school,session);return result(List.of(col("faculty","Faculty/Day"),col("day","Day"),col("freePeriods","Free Periods")),rows);}
    private Map<String,Object> facultyReport(String school,String session,Map<String,String> p){String uid=value(p,"ttUid");int only=intValue(p.get("facultyId"));List<Map<String,Object>> periods=safe("SELECT lecture_id AS id,lecture_name AS name FROM TB_Timetables_config WHERE school_id=? AND session=? AND TT_UID=? ORDER BY TRY_CONVERT(int,lecture_id)",school,session,uid);List<Map<String,Object>> facultyRows=safe("SELECT DISTINCT f.F_id AS id,UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS name FROM TB_FacultyMaster f JOIN tb_faculty_withdrawn w ON w.f_id=f.F_id AND w.school_id=f.school_id AND w.session=? AND w.status='Registered' WHERE f.school_id=? "+(only>0?"AND f.F_id=?":"")+" ORDER BY name",only>0?new Object[]{session,school,only}:new Object[]{session,school});List<Map<String,Object>> assigned=safe("SELECT t.F_id AS facultyId,t.TDay AS day,t.Period AS period,sm.Subject AS subject,cn.class_name+' ('+cl.Section+')' AS className FROM TB_TimeTable t LEFT JOIN TB_SubjectMaster sm ON sm.school_id=t.school_id AND sm.id=t.Subject_id LEFT JOIN TB_Class cl ON cl.school_id=t.school_id AND cl.Sec_id=t.sec_id LEFT JOIN class_name cn ON cn.school_id=cl.school_id AND cn.id=cl.class_id WHERE t.school_id=? AND t.session=? AND t.TT_UID=?",school,session,uid);List<Map<String,Object>> rows=new ArrayList<>();for(Map<String,Object> f:facultyRows){Map<String,Object> row=new LinkedHashMap<>();row.put("facultyName",f.get("name"));for(String day:DAYS){for(Map<String,Object> per:periods){String key=day+"_"+text(per.get("id"));Map<String,Object> hit=assigned.stream().filter(x->integer(x.get("facultyId"))==integer(f.get("id"))&&day.equals(text(x.get("day")))&&text(per.get("id")).equals(text(x.get("period")))).findFirst().orElse(null);row.put(key+"_Class",hit==null?"":text(hit.get("className")));row.put(key+"_Subject",hit==null?"":text(hit.get("subject")));}}rows.add(row);}List<Map<String,Object>> columns=new ArrayList<>();columns.add(col("facultyName","Faculty Name"));for(String day:DAYS)for(Map<String,Object> per:periods){String key=day+"_"+text(per.get("id"));columns.add(col(key+"_Class",day+" "+text(per.get("name"))+" Class"));columns.add(col(key+"_Subject",day+" "+text(per.get("name"))+" Subject"));}return result(columns,rows);}
    private Map<String,Object> schoolReport(String school,String session,Map<String,String> p){String uid=value(p,"ttUid");List<Map<String,Object>> periods=safe("SELECT lecture_id AS id,lecture_name AS name FROM TB_Timetables_config WHERE school_id=? AND session=? AND TT_UID=? ORDER BY TRY_CONVERT(int,lecture_id)",school,session,uid);List<Map<String,Object>> classRows=safe("SELECT c.Sec_id AS id,cn.class_name+' ('+c.Section+')' AS name FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id WHERE c.school_id=? ORDER BY c.class_id,c.Sec_id",school);List<Map<String,Object>> assigned=safe("SELECT t.sec_id AS sectionId,t.TDay AS day,t.Period AS period,sm.Subject AS subject,UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS facultyName FROM TB_TimeTable t LEFT JOIN TB_SubjectMaster sm ON sm.school_id=t.school_id AND sm.id=t.Subject_id LEFT JOIN TB_FacultyMaster f ON f.school_id=t.school_id AND f.F_id=t.F_id WHERE t.school_id=? AND t.session=? AND t.TT_UID=?",school,session,uid);List<Map<String,Object>> rows=new ArrayList<>();for(Map<String,Object> c:classRows){Map<String,Object> row=new LinkedHashMap<>();row.put("className",c.get("name"));for(String day:DAYS)for(Map<String,Object> per:periods){String key=day+"_"+text(per.get("id"));Map<String,Object> hit=assigned.stream().filter(x->integer(x.get("sectionId"))==integer(c.get("id"))&&day.equals(text(x.get("day")))&&text(per.get("id")).equals(text(x.get("period")))).findFirst().orElse(null);row.put(key+"_Subject",hit==null?"":text(hit.get("subject")));row.put(key+"_Faculty",hit==null?"":text(hit.get("facultyName")));}rows.add(row);}List<Map<String,Object>> columns=new ArrayList<>();columns.add(col("className","Class/Section"));for(String day:DAYS)for(Map<String,Object> per:periods){String key=day+"_"+text(per.get("id"));columns.add(col(key+"_Subject",day+" "+text(per.get("name"))+" Subject"));columns.add(col(key+"_Faculty",day+" "+text(per.get("name"))+" Faculty"));}return result(columns,rows);}
    private Map<String,Object> timeReport(String school,String session,Map<String,String> p){String uid=value(p,"ttUid");int section=intValue(p.get("sectionId")),fid=intValue(p.get("facultyId"));List<Map<String,Object>> rows=matrixRows(school,session,uid,fid>0?"faculty":"class",fid>0?fid:section);return result(dynamicColumns(rows,"day/period"),rows);}
    private List<Map<String,Object>> matrixRows(String school,String session,String uid,String mode,int id){List<Map<String,Object>> periods=safe("SELECT lecture_id AS id,lecture_name AS name,ST_Time_Hr+':'+ST_Time_Min+' '+ST_Time_Mode+' - '+END_Time_Hr+':'+END_Time_Min+' '+END_Time_Mode AS time FROM TB_Timetables_config WHERE school_id=? AND session=? AND TT_UID=? ORDER BY TRY_CONVERT(int,lecture_id)",school,session,uid);List<Map<String,Object>> out=new ArrayList<>();for(String day:List.of("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")){Map<String,Object> row=new LinkedHashMap<>();row.put("day/period",day);for(Map<String,Object> per:periods){String pid=text(per.get("id"));String sql="SELECT TOP 1 UPPER(COALESCE(sm.Subject,'')+' - '+LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) FROM TB_TimeTable t LEFT JOIN TB_SubjectMaster sm ON sm.school_id=t.school_id AND sm.id=t.Subject_id LEFT JOIN TB_FacultyMaster f ON f.school_id=t.school_id AND f.F_id=t.F_id WHERE t.school_id=? AND t.session=? AND t.TDay=? AND t.Period=? AND t.TT_UID=? AND t."+(mode.equals("faculty")?"F_id":"sec_id")+"=?";String val=first(sql,school,session,day,pid,uid,id);row.put(text(per.get("name"))+" ("+text(per.get("time"))+")",val.isBlank()?"N.A.":val);}out.add(row);}return out;}
    private Map<String,Object> virtualReport(String school,String session,Map<String,String> p){String where=" WHERE v.school_id=? AND v.session=? ";List<Object>args=new ArrayList<>(List.of(school,session));if(intValue(p.get("subjectId"))>0){where+=" AND v.subject_id=?";args.add(intValue(p.get("subjectId")));}if(intValue(p.get("facultyId"))>0){where+=" AND v.f_id=?";args.add(intValue(p.get("facultyId")));}if(intValue(p.get("classId"))>0){where+=" AND c.class_id=?";args.add(intValue(p.get("classId")));}if(intValue(p.get("sectionId"))>0){where+=" AND v.sec_id=?";args.add(intValue(p.get("sectionId")));}List<Map<String,Object>> rows=safe("SELECT v.VClass_id AS vClassId,v.V_Class AS vClassName,sm.Subject AS subjectName,UPPER(LTRIM(RTRIM(COALESCE(f.First_name,'')+' '+COALESCE(f.Mid_name,'')+' '+COALESCE(f.Last_name,'')))) AS facultyName,COUNT(v.Stu_id) AS studentCount FROM TB_VirtualClass v LEFT JOIN TB_SubjectMaster sm ON sm.school_id=v.school_id AND sm.id=v.subject_id LEFT JOIN TB_FacultyMaster f ON f.school_id=v.school_id AND f.F_id=v.f_id LEFT JOIN TB_Class c ON c.school_id=v.school_id AND c.Sec_id=v.sec_id "+where+" GROUP BY v.VClass_id,v.V_Class,sm.Subject,f.First_name,f.Mid_name,f.Last_name ORDER BY v.V_Class",args.toArray());return result(List.of(col("vClassId","Virtual Class ID"),col("vClassName","Virtual Class"),col("subjectName","Subject"),col("facultyName","Faculty"),col("studentCount","Student Count")),rows);}

    private Map<String,Object> result(List<Map<String,Object>> columns,List<Map<String,Object>> rows){Map<String,Object> r=new LinkedHashMap<>();r.put("columns",columns);r.put("rows",rows);r.put("count",rows.size());return r;}
    private List<Map<String,Object>> dynamicColumns(List<Map<String,Object>> rows,String first){List<Map<String,Object>> c=new ArrayList<>();if(!rows.isEmpty())for(String k:rows.get(0).keySet())c.add(col(k,k));else c.add(col(first,first));return c;}
    private Map<String,Object> col(String key,String label){Map<String,Object>m=new LinkedHashMap<>();m.put("key",key);m.put("label",label);return m;}
    private String defaultSession(String school){String x=first("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC",school);return x.isBlank()?first("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY id DESC",school):x;}
    private List<Map<String,Object>> safe(String sql,Object... args){try{return jdbc.queryForList(sql,args);}catch(DataAccessException ex){return new ArrayList<>();}}
    private String first(String sql,Object...args){try{String v=jdbc.queryForObject(sql,String.class,args);return v==null?"":v;}catch(Exception e){return "";}}
    private int scalar(String sql,Object...args){try{Integer v=jdbc.queryForObject(sql,Integer.class,args);return v==null?0:v;}catch(Exception e){return 0;}}
    private static String text(Object v){return v==null?"":String.valueOf(v);}
    private static String legacyDate(String value){try{return LocalDate.parse(value,DateTimeFormatter.ISO_LOCAL_DATE).format(DateTimeFormatter.ofPattern("dd MMM yyyy",Locale.ENGLISH));}catch(Exception ignored){return value;}}
    private static int integer(Object v){try{return Integer.parseInt(text(v).replaceAll("\\.0+$",""));}catch(Exception e){return 0;}}
    private static int intValue(String v){return integer(v);}
    private static String value(Map<String,String> m,String k){return m==null?"":text(m.get(k));}
    @SuppressWarnings("unchecked") private static List<Map<String,Object>> rows(Map<String,Object>b){Object v=b.get("rows");return v instanceof List<?> l?(List<Map<String,Object>>)(List<?>)l:List.of();}
    private static List<?> list(Object v){return v instanceof List<?> l?l:List.of();}
}
