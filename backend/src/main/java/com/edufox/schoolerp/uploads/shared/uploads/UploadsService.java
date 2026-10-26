package com.edufox.schoolerp.uploads.shared.uploads;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UploadsService {
    private final JdbcTemplate jdbc;
    private final UploadsRepository repo;
    private final DatabaseWriteGuard writeGuard;
    private final Path storageRoot = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();

    public Map<String,Object> bootstrap(String school, String session, String staff) {
        if (UploadsRepository.blank(session)) session = defaultSession(school);
        Map<String,Object> out = new LinkedHashMap<>();
        out.put("sessions", repo.sessions(school));
        out.put("session", session);
        out.put("classes", repo.classes(school, session, staff));
        out.put("faculties", repo.faculties(school));
        out.put("schoolId", school);
        return out;
    }

    public String defaultSession(String school) {
        List<Map<String,Object>> rows = repo.sessions(school);
        return rows.isEmpty() ? "" : Objects.toString(rows.get(0).get("value"), "");
    }

    public List<Map<String,Object>> sections(String school,String session,String classId){return repo.sections(school,session,classId);}
    public List<Map<String,Object>> subjects(String school,String session,String secId,String classId){return repo.subjects(school,session,secId,classId);}
    public List<Map<String,Object>> students(String school,String session,String secId){return repo.students(school,session,secId);}
    public List<Map<String,Object>> terms(String school,String session,String classId){return repo.terms(school,session,classId);}
    public List<Map<String,Object>> exams(String school,String session,String classId,String termId){return repo.exams(school,session,classId,termId);}

    public List<Map<String,Object>> planners(String school,String session){return repo.planners(school,session);}
    @Transactional public Map<String,Object> savePlanner(String school,String staff,String session,String title,MultipartFile file){
        writeGuard.requireWritable(); required(session,"Session"); required(title,"Title"); requiredFile(file);
        duplicate("SELECT COUNT(1) FROM academic_planner WHERE school_id=? AND session=? AND title=?",school,session,title,"Title Already Exists");
        String path=saveFile(file,school,"academic_planner",Set.of("pdf","doc","docx","jpeg","jpg","png"));
        jdbc.update("INSERT INTO academic_planner(title,file_path,session,school_id,created_by,created_date,modified_by,modified_date) VALUES(?,?,?,?,?,GETDATE(),?,GETDATE())",title,path,session,school,staff,staff);
        return msg("Record Saved Successfully");
    }
    @Transactional public Map<String,Object> updatePlanner(String school,String staff,String session,String id,String title,MultipartFile file){
        writeGuard.requireWritable(); required(title,"Title");
        String path=file==null||file.isEmpty()?null:saveFile(file,school,"academic_planner",Set.of("pdf","doc","docx","jpeg","jpg","png"));
        if(path==null) jdbc.update("UPDATE academic_planner SET title=?,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND id=?",title,staff,school,session,id);
        else jdbc.update("UPDATE academic_planner SET title=?,file_path=?,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND id=?",title,path,staff,school,session,id);
        return msg("Record Updated Successfully");
    }
    public Map<String,Object> deletePlanner(String school,String id){writeGuard.requireWritable();jdbc.update("DELETE FROM academic_planner WHERE school_id=? AND id=?",school,id);return msg("Record Deleted Successfully");}

    public List<Map<String,Object>> assignments(String school,String session,String staff){return repo.assignments(school,session,staff);}
    @Transactional public Map<String,Object> saveAssignment(String school,String staff,String session,String secId,String classId,String className,String subjectId,String studentIds,String postedDate,String submissionDate,String message,MultipartFile file){
        writeGuard.requireWritable(); required(session,"Session"); required(secId,"Class"); required(subjectId,"Subject"); required(postedDate,"Posted Date"); required(submissionDate,"Submission Date"); required(message,"Message");
        List<Integer> ids=integers(studentIds); if(ids.isEmpty()) throw bad("No students found for selected class.");
        if(file!=null&&!file.isEmpty()&&file.getSize()>10*1024*1024) throw bad("File size too large.");
        String path=file==null||file.isEmpty()?"":saveFile(file,school,"Assignment/"+school,Set.of("pdf","doc","docx","jpg","jpeg","png","xls","xlsx","txt"));
        String assignId=String.valueOf(repo.nextNumeric("TB_AssignmentMaster","Assign_id",school));
        for(Integer student:ids){
            jdbc.update("INSERT INTO TB_AssignmentMaster(Assign_id,F_id,Stu_id,Message,PostedDate,SubmissionDate,Read_Status,FilePath,session,school_id,created_by,created_date,status,class_name,subject_id,original_filename,sec_id) VALUES(?,?,?,?,?,?,'',?,?,?, ?,GETDATE(),1,?,?,?,?)",assignId,facultyForStaff(school,staff),student,message,toSqlDate(postedDate),toSqlDate(submissionDate),path,session,school,staff,className,subjectId,file==null?"":file.getOriginalFilename(),secId);
        }
        return msg("Assignment uploaded successfully.");
    }
    public Map<String,Object> deleteAssignment(String school,String assignId){writeGuard.requireWritable();jdbc.update("UPDATE TB_AssignmentMaster SET status=0,modified_date=GETDATE() WHERE school_id=? AND Assign_id=?",school,assignId);return msg("Assignment deleted successfully.");}

    public List<Map<String,Object>> dailyHomeworkView(String school,String session,String secId,String date){return repo.dailyHomeworkView(school,session,secId,date);}
    public List<Map<String,Object>> dailyHomeworkSubjects(String school,String session,String secId,String classId,String date){return repo.dailyHomeworkSubjects(school,session,secId,classId,date);}
    @Transactional public Map<String,Object> saveDailyHomework(String school,String staff,String session,String secId,String date,List<Map<String,Object>> rows,List<MultipartFile> files){
        writeGuard.requireWritable(); required(secId,"Class"); required(date,"Date");
        int saved=0;
        for(int i=0;i<(rows==null?0:rows.size());i++){
            Map<String,Object> row=rows.get(i); String subject=text(row,"subjectId"); String desc=text(row,"homeworkDesc");
            MultipartFile file=files!=null&&i<files.size()?files.get(i):null;
            if(UploadsRepository.blank(subject)|| (UploadsRepository.blank(desc)&&(file==null||file.isEmpty()))) continue;
            String path=file==null||file.isEmpty()?null:saveFile(file,school,"daily_homework",Set.of("pdf","doc","docx","jpg","jpeg","png","gif","txt"));
            List<Map<String,Object>> existing=jdbc.queryForList("SELECT TOP 1 id,file_path FROM daily_homework WHERE school_id=? AND session=? AND sec_id=? AND subject_id=? AND (homework_date=? OR TRY_CONVERT(date,homework_date)=TRY_CONVERT(date,?)) ORDER BY id DESC",school,session,secId,subject,date,date);
            if(existing.isEmpty()) jdbc.update("INSERT INTO daily_homework(sec_id,homework_desc,homework_date,subject_id,session,school_id,created_date,created_by,modified_date,modified_by,file_path) VALUES(?,?,?,?,?, ?,GETDATE(),?,GETDATE(),?,?)",secId,desc,date,subject,session,school,staff,staff,path==null?"":path);
            else if(path==null) jdbc.update("UPDATE daily_homework SET homework_desc=?,modified_date=GETDATE(),modified_by=? WHERE id=? AND school_id=?",desc,staff,existing.get(0).get("id"),school);
            else jdbc.update("UPDATE daily_homework SET homework_desc=?,file_path=?,modified_date=GETDATE(),modified_by=? WHERE id=? AND school_id=?",desc,path,staff,existing.get(0).get("id"),school);
            saved++;
        }
        return row("message",saved==0?"Nothing to submit. Make a change first.":"Homework uploaded successfully.","saved",saved);
    }
    public Map<String,Object> deleteDailyHomework(String school,String id){writeGuard.requireWritable();jdbc.update("DELETE FROM daily_homework WHERE school_id=? AND id=?",school,id);return msg("Homework deleted successfully.");}

    public List<Map<String,Object>> holidays(String school,String session){return repo.holidayHomework(school,session);}
    public String holidayLink(String school,String session){return repo.holidayLink(school,session);}
    @Transactional public Map<String,Object> saveHoliday(String school,String staff,String session,String classId,String sectionIds,String linkTitle,String title,String submissionDate,MultipartFile file){
        writeGuard.requireWritable();required(classId,"Class");required(sectionIds,"Section");required(title,"Title");requiredFile(file);
        String path=saveFile(file,school,"holiday_homework",Set.of("pdf"));
        if(jdbc.update("UPDATE holiday_link SET link_title=?,modified_by=?,modified_date=GETDATE(),status=1 WHERE school_id=? AND session=?",linkTitle,staff,school,session)==0) jdbc.update("INSERT INTO holiday_link(link_title,created_date,created_by,school_id,session,status) VALUES(?,GETDATE(),?,?,?,1)",linkTitle,staff,school,session);
        long titleId=repo.nextNumeric("holiday_homework","title_id",school);
        for(Integer sec:integers(sectionIds)) jdbc.update("INSERT INTO holiday_homework(title_id,title,class_id,sec_id,file_path,created_date,created_by,modified_date,modified_by,school_id,session,status,f_id,submission_date) VALUES(?,?,?,?,?,GETDATE(),?,GETDATE(),?,?,?,1,?,?)",titleId,title,classId,sec,path,staff,staff,school,session,facultyForStaff(school,staff),submissionDate);
        return msg("Record Saved Successfully");
    }
    public Map<String,Object> deleteHoliday(String school,String session,String titleId,String staff){writeGuard.requireWritable();jdbc.update("UPDATE holiday_homework SET status=0,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND title_id=?",staff,school,session,titleId);return msg("Record Deleted Successfully");}

    public List<Map<String,Object>> syllabus(String school,String session){return repo.syllabi(school,session);}
    @Transactional public Map<String,Object> saveSyllabus(String school,String staff,String session,String title,String classId,String secId,String termId,String examId,MultipartFile file){
        writeGuard.requireWritable(); required(title,"Title"); required(classId,"Class"); required(termId,"Term"); required(examId,"Exam"); requiredFile(file);
        duplicate("SELECT COUNT(1) FROM upload_syllabus WHERE school_id=? AND session=? AND class_id=? AND term_id=? AND exam_type_id=? AND title=? AND ISNULL(status,1)=1",school,session,classId,termId,examId,title,"Syllabus Title Already Exists");
        String path=saveFile(file,school,"Syllabus/"+school,Set.of("pdf","docx","doc","jpg","jpeg","gif","png","txt"));
        long id=repo.nextNumeric("upload_syllabus","syllabus_id",school);
        jdbc.update("INSERT INTO upload_syllabus(syllabus_id,title,file_path,class_id,created_date,created_by,modified_date,modified_by,school_id,session,status,sec_id,term_id,exam_type_id) VALUES(?,?,?, ?,GETDATE(),?,GETDATE(),?,?,?,1,?,?,?)",id,title,path,classId,staff,staff,school,session,secId,termId,examId);
        return msg("Record Saved Successfully");
    }
    @Transactional public Map<String,Object> updateSyllabus(String school,String staff,String session,String id,String title,String classId,String secId,String termId,String examId,MultipartFile file){
        writeGuard.requireWritable();required(title,"Title");String path=file==null||file.isEmpty()?null:saveFile(file,school,"Syllabus/"+school,Set.of("pdf","docx","doc","jpg","jpeg","gif","png","txt"));
        if(path==null) jdbc.update("UPDATE upload_syllabus SET title=?,class_id=?,sec_id=?,term_id=?,exam_type_id=?,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND syllabus_id=?",title,classId,secId,termId,examId,staff,school,session,id);
        else jdbc.update("UPDATE upload_syllabus SET title=?,file_path=?,class_id=?,sec_id=?,term_id=?,exam_type_id=?,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND syllabus_id=?",title,path,classId,secId,termId,examId,staff,school,session,id);
        return msg("Record updated Successfully");
    }
    public Map<String,Object> deleteSyllabus(String school,String session,String id,String staff){writeGuard.requireWritable();jdbc.update("UPDATE upload_syllabus SET status=0,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND syllabus_id=?",staff,school,session,id);return msg("Record Deleted Successfully");}

    public List<Map<String,Object>> calendar(String school,String session){return repo.calendar(school,session);}
    @Transactional public Map<String,Object> saveCalendar(String school,String staff,String session,String id,String from,String to,String title,boolean update){
        writeGuard.requireWritable();required(from,"Start Date");required(to,"End Date");required(title,"Title");
        if(update) jdbc.update("UPDATE school_calender SET event_date=?,event_to_date=?,event_name=?,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND id=?",from,to,title,staff,school,session,id);
        else jdbc.update("INSERT INTO school_calender(event_date,event_to_date,event_name,school_id,session,created_by,created_date,status) VALUES(?,?,?, ?,?,?,GETDATE(),1)",from,to,title,school,session,staff);
        return msg(update?"record updated successfully":"record saved successfully");
    }
    public Map<String,Object> deleteCalendar(String school,String id){writeGuard.requireWritable();jdbc.update("UPDATE school_calender SET status=0,modified_date=GETDATE() WHERE school_id=? AND id=?",school,id);return msg("Record Deleted Successfully");}

    public List<Map<String,Object>> activities(String school,String session){return repo.activities(school,session);}
    @Transactional public Map<String,Object> saveActivity(String school,String staff,String session,String id,String date,String name,boolean update){
        writeGuard.requireWritable();required(date,"Date");required(name,"Activity");
        Integer duplicate=update?jdbc.queryForObject("SELECT COUNT(1) FROM Activity WHERE school_id=? AND session=? AND activity_name=? AND activity_date=? AND activity_id<>?",Integer.class,school,session,name,date,id):jdbc.queryForObject("SELECT COUNT(1) FROM Activity WHERE school_id=? AND activity_name=? AND activity_date=?",Integer.class,school,name,date);
        if(duplicate!=null&&duplicate>0) throw bad("Activity Name Already Exists");
        if(update) jdbc.update("UPDATE Activity SET activity_date=?,activity_name=?,modified_by=?,modified_date=GETDATE() WHERE school_id=? AND session=? AND activity_id=?",date,name,staff,school,session,id);
        else jdbc.update("INSERT INTO Activity(activity_id,activity_date,activity_name,created_date,created_by,modified_date,modified_by,school_id,session) VALUES(?,?,?,GETDATE(),?,GETDATE(),?,?,?)",repo.nextNumeric("Activity","activity_id",school),date,name,staff,staff,school,session);
        return msg(update?"Record Updated Successfully":"Record Inserted Successfully");
    }

    public List<Map<String,Object>> plannerReport(String school,String session){return repo.planners(school,session);}
    public List<Map<String,Object>> assignmentReport(String school,String session,String staff){
        List<Map<String,Object>> flat=repo.assignmentReportAll(school,session); Map<String,Map<String,Object>> grouped=new LinkedHashMap<>();
        for(Map<String,Object> item:flat){String key=Objects.toString(item.get("secId"),"");Map<String,Object> section=grouped.computeIfAbsent(key,k->{Map<String,Object> x=new LinkedHashMap<>();x.put("id",item.get("secId"));x.put("secId",item.get("secId"));x.put("classId",item.get("classId"));x.put("label",item.get("label"));x.put("details",new ArrayList<Map<String,Object>>());return x;});((List<Map<String,Object>>)section.get("details")).add(new LinkedHashMap<>(item));}
        return new ArrayList<>(grouped.values());
    }
    public List<Map<String,Object>> dailyReport(String school,String session,String secId,String date){return repo.homeworkDetails(school,session,secId,date);}
    public List<Map<String,Object>> dailyReportSections(String school,String session,String staff){return repo.classes(school,session,staff);}
    public Map<String,Object> mobileReport(String school,String session,String mode,String classId,String secId,String status){
        Map<String,Object> out=new LinkedHashMap<>(); if("class".equalsIgnoreCase(mode)) out.put("rows",repo.mobileCoverageClass(school,session)); else out.put("rows",repo.mobileCoverageDetails(school,session,classId,secId,status)); return out;
    }
    public List<Map<String,Object>> showHomeworkDates(String school,String session,String secId){return repo.homeworkDates(school,session,secId);}
    public List<Map<String,Object>> showHomeworkDetails(String school,String session,String secId,String date){return repo.homeworkDetails(school,session,secId,date);}
    public List<Map<String,Object>> showSyllabusClasses(String school,String session,String staff){return repo.syllabusClasses(school,session,staff);}
    public List<Map<String,Object>> showSyllabusTitles(String school,String session,String classId){return repo.syllabusTitles(school,session,classId);}

    public Resource file(String relative){
        String clean=relative==null?"":relative.replace('\\','/').replaceFirst("^/+","").replaceFirst("^~/","");
        Path path=storageRoot.resolve(clean).normalize();
        Path legacy=Paths.get(System.getProperty("user.dir")).getParent().getParent().resolve("Old").normalize().resolve(clean).normalize();
        if(!Files.isRegularFile(path)&&legacy.startsWith(Paths.get(System.getProperty("user.dir")).getParent().getParent().resolve("Old").normalize()))path=legacy;
        if(!Files.isRegularFile(path)||(!path.startsWith(storageRoot)&&!path.toString().contains("\\Old\\")))throw new ResponseStatusException(NOT_FOUND,"File not found");
        return new FileSystemResource(path);
    }

    private int facultyForStaff(String school,String staff){Integer f=jdbc.queryForObject("SELECT TOP 1 f_id FROM TB_FacultyMaster WHERE school_id=? AND staff_u_id=?",Integer.class,school,staff);return f==null?0:f;}
    private String saveFile(MultipartFile file,String school,String folder,Set<String> allowed){
        if(file==null||file.isEmpty())return "";String ext=extension(file.getOriginalFilename());if(!allowed.contains(ext))throw bad("Unsupported file type");String original=file.getOriginalFilename()==null?"file":Paths.get(file.getOriginalFilename()).getFileName().toString();String base=original.replaceFirst("\\.[^.]+$","").replaceAll("[^A-Za-z0-9._-]","_");String name=base+"_"+UUID.randomUUID().toString().replace("-","").substring(0,10)+"_"+school+"."+ext;Path dir=storageRoot.resolve("Uploads_doc").resolve(folder).normalize();try{Files.createDirectories(dir);Files.copy(file.getInputStream(),dir.resolve(name),StandardCopyOption.REPLACE_EXISTING);}catch(IOException e){throw new ResponseStatusException(INTERNAL_SERVER_ERROR,"Could not save upload",e);}return "Uploads_doc/"+folder+"/"+name;}
    private static String extension(String name){return name==null||!name.contains(".")?"":name.substring(name.lastIndexOf('.')+1).toLowerCase(Locale.ROOT);}
    private static List<Integer> integers(String raw){if(UploadsRepository.blank(raw))return List.of();List<Integer> out=new ArrayList<>();for(String x:raw.split(","))try{int i=Integer.parseInt(x.trim());if(i>0)out.add(i);}catch(Exception ignored){}return out;}
    private static void required(String value,String label){if(UploadsRepository.blank(value)||"0".equals(value))throw bad(label+" is required");}
    private static void requiredFile(MultipartFile file){if(file==null||file.isEmpty())throw bad("Please Upload File");}
    private void duplicate(String sql,Object... args){String message=String.valueOf(args[args.length-1]);Object[] values=Arrays.copyOf(args,args.length-1);Integer n=jdbc.queryForObject(sql,Integer.class,values);if(n!=null&&n>0)throw bad(message);}
    private static String text(Map<String,Object> row,String key){return row.get(key)==null?"":String.valueOf(row.get(key));}
    private static java.sql.Date toSqlDate(String value){try{if(value==null||value.isBlank())return null;return java.sql.Date.valueOf(value.substring(0,10));}catch(Exception e){throw bad("Invalid date format");}}
    private static ResponseStatusException bad(String message){return new ResponseStatusException(BAD_REQUEST,message);}
    private static Map<String,Object> msg(String message){return row("message",message);}
    private static Map<String,Object> row(Object... values){Map<String,Object> out=new LinkedHashMap<>();for(int i=0;i+1<values.length;i+=2)out.put(String.valueOf(values[i]),values[i+1]);return out;}
}
