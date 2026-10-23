package com.edufox.schoolerp.uploads;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.*;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class UploadsController {
    private final UploadsService service;
    private final ObjectMapper mapper;

    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(@RequestParam(defaultValue="") String session,Authentication a){return service.bootstrap(school(a),session,staff(a));}
    @GetMapping("/sections") public List<Map<String,Object>> sections(@RequestParam String session,@RequestParam String classId,Authentication a){return service.sections(school(a),session,classId);}
    @GetMapping("/subjects") public List<Map<String,Object>> subjects(@RequestParam String session,@RequestParam String secId,@RequestParam String classId,Authentication a){return service.subjects(school(a),session,secId,classId);}
    @GetMapping("/students") public List<Map<String,Object>> students(@RequestParam String session,@RequestParam String secId,Authentication a){return service.students(school(a),session,secId);}
    @GetMapping("/terms") public List<Map<String,Object>> terms(@RequestParam String session,@RequestParam String classId,Authentication a){return service.terms(school(a),session,classId);}
    @GetMapping("/exams") public List<Map<String,Object>> exams(@RequestParam String session,@RequestParam String classId,@RequestParam String termId,Authentication a){return service.exams(school(a),session,classId,termId);}

    @GetMapping("/planners") public List<Map<String,Object>> planners(@RequestParam String session,Authentication a){return service.planners(school(a),session);}
    @PostMapping(value="/planners",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> savePlanner(@RequestParam String session,@RequestParam String title,@RequestPart("file") MultipartFile file,Authentication a){return service.savePlanner(school(a),staff(a),session,title,file);}
    @PutMapping(value="/planners/{id}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> updatePlanner(@PathVariable String id,@RequestParam String session,@RequestParam String title,@RequestPart(value="file",required=false) MultipartFile file,Authentication a){return service.updatePlanner(school(a),staff(a),session,id,title,file);}
    @DeleteMapping("/planners/{id}") public Map<String,Object> deletePlanner(@PathVariable String id,Authentication a){return service.deletePlanner(school(a),id);}

    @GetMapping("/assignments") public List<Map<String,Object>> assignments(@RequestParam String session,Authentication a){return service.assignments(school(a),session,staff(a));}
    @PostMapping(value="/assignments",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveAssignment(@RequestParam String session,@RequestParam String secId,@RequestParam String classId,@RequestParam String className,@RequestParam String subjectId,@RequestParam String studentIds,@RequestParam String postedDate,@RequestParam String submissionDate,@RequestParam String message,@RequestPart(value="file",required=false) MultipartFile file,Authentication a){return service.saveAssignment(school(a),staff(a),session,secId,classId,className,subjectId,studentIds,postedDate,submissionDate,message,file);}
    @DeleteMapping("/assignments/{assignId}") public Map<String,Object> deleteAssignment(@PathVariable String assignId,Authentication a){return service.deleteAssignment(school(a),assignId);}

    @GetMapping("/daily-homework/view") public List<Map<String,Object>> dailyView(@RequestParam String session,@RequestParam String secId,@RequestParam String date,Authentication a){return service.dailyHomeworkView(school(a),session,secId,date);}
    @GetMapping("/daily-homework/subjects") public List<Map<String,Object>> dailySubjects(@RequestParam String session,@RequestParam String secId,@RequestParam String classId,@RequestParam String date,Authentication a){return service.dailyHomeworkSubjects(school(a),session,secId,classId,date);}
    @PostMapping(value="/daily-homework",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveDaily(@RequestParam String session,@RequestParam String secId,@RequestParam String date,@RequestParam String rows,@RequestPart(value="files",required=false) List<MultipartFile> files,Authentication a){return service.saveDailyHomework(school(a),staff(a),session,secId,date,parseRows(rows),files);}
    @DeleteMapping("/daily-homework/{id}") public Map<String,Object> deleteDaily(@PathVariable String id,Authentication a){return service.deleteDailyHomework(school(a),id);}

    @GetMapping("/holiday-homework") public Map<String,Object> holiday(@RequestParam String session,Authentication a){return map("rows",service.holidays(school(a),session),"linkTitle",service.holidayLink(school(a),session));}
    @PostMapping(value="/holiday-homework",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveHoliday(@RequestParam String session,@RequestParam String classId,@RequestParam String sectionIds,@RequestParam(defaultValue="") String linkTitle,@RequestParam String title,@RequestParam(defaultValue="") String submissionDate,@RequestPart("file") MultipartFile file,Authentication a){return service.saveHoliday(school(a),staff(a),session,classId,sectionIds,linkTitle,title,submissionDate,file);}
    @DeleteMapping("/holiday-homework/{titleId}") public Map<String,Object> deleteHoliday(@PathVariable String titleId,@RequestParam String session,Authentication a){return service.deleteHoliday(school(a),session,titleId,staff(a));}

    @GetMapping("/syllabus") public List<Map<String,Object>> syllabus(@RequestParam String session,Authentication a){return service.syllabus(school(a),session);}
    @PostMapping(value="/syllabus",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveSyllabus(@RequestParam String session,@RequestParam String title,@RequestParam String classId,@RequestParam(defaultValue="0") String secId,@RequestParam String termId,@RequestParam String examId,@RequestPart("file") MultipartFile file,Authentication a){return service.saveSyllabus(school(a),staff(a),session,title,classId,secId,termId,examId,file);}
    @PutMapping(value="/syllabus/{id}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> updateSyllabus(@PathVariable String id,@RequestParam String session,@RequestParam String title,@RequestParam String classId,@RequestParam(defaultValue="0") String secId,@RequestParam String termId,@RequestParam String examId,@RequestPart(value="file",required=false) MultipartFile file,Authentication a){return service.updateSyllabus(school(a),staff(a),session,id,title,classId,secId,termId,examId,file);}
    @DeleteMapping("/syllabus/{id}") public Map<String,Object> deleteSyllabus(@PathVariable String id,@RequestParam String session,Authentication a){return service.deleteSyllabus(school(a),session,id,staff(a));}

    @GetMapping("/calendar") public List<Map<String,Object>> calendar(@RequestParam String session,Authentication a){return service.calendar(school(a),session);}
    @PostMapping("/calendar") public Map<String,Object> saveCalendar(@RequestBody Map<String,Object> body,Authentication a){return service.saveCalendar(school(a),staff(a),text(body,"session"),text(body,"id"),text(body,"from"),text(body,"to"),text(body,"title"),"update".equalsIgnoreCase(text(body,"mode")));}
    @DeleteMapping("/calendar/{id}") public Map<String,Object> deleteCalendar(@PathVariable String id,Authentication a){return service.deleteCalendar(school(a),id);}
    @GetMapping("/activities") public List<Map<String,Object>> activities(@RequestParam String session,Authentication a){return service.activities(school(a),session);}
    @PostMapping("/activities") public Map<String,Object> saveActivity(@RequestBody Map<String,Object> body,Authentication a){return service.saveActivity(school(a),staff(a),text(body,"session"),text(body,"id"),text(body,"date"),text(body,"name"),"update".equalsIgnoreCase(text(body,"mode")));}

    @GetMapping("/reports/academic-planner") public List<Map<String,Object>> plannerReport(@RequestParam String session,Authentication a){return service.plannerReport(school(a),session);}
    @GetMapping("/reports/assignment") public List<Map<String,Object>> assignmentReport(@RequestParam String session,Authentication a){return service.assignmentReport(school(a),session,staff(a));}
    @GetMapping("/reports/daily-homework") public Map<String,Object> dailyReport(@RequestParam String session,@RequestParam String secId,@RequestParam String date,Authentication a){return map("rows",service.dailyReport(school(a),session,secId,date));}
    @GetMapping("/reports/daily-homework/classes") public List<Map<String,Object>> dailyReportClasses(@RequestParam String session,Authentication a){return service.dailyReportSections(school(a),session,staff(a));}
    @GetMapping("/reports/mobile-coverage") public Map<String,Object> mobileReport(@RequestParam String session,@RequestParam(defaultValue="detail") String mode,@RequestParam(defaultValue="") String classId,@RequestParam(defaultValue="") String secId,@RequestParam(defaultValue="all") String status,Authentication a){return service.mobileReport(school(a),session,mode,classId,secId,status);}
    @GetMapping("/reports/homework-dates") public List<Map<String,Object>> homeworkDates(@RequestParam String session,@RequestParam String secId,Authentication a){return service.showHomeworkDates(school(a),session,secId);}
    @GetMapping("/reports/homework-details") public List<Map<String,Object>> homeworkDetails(@RequestParam String session,@RequestParam String secId,@RequestParam String date,Authentication a){return service.showHomeworkDetails(school(a),session,secId,date);}
    @GetMapping("/reports/syllabus/classes") public List<Map<String,Object>> syllabusClasses(@RequestParam String session,Authentication a){return service.showSyllabusClasses(school(a),session,staff(a));}
    @GetMapping("/reports/syllabus/titles") public List<Map<String,Object>> syllabusTitles(@RequestParam String session,@RequestParam String classId,Authentication a){return service.showSyllabusTitles(school(a),session,classId);}

    @GetMapping("/files") public ResponseEntity<Resource> file(@RequestParam String path){Resource r=service.file(path);try{String type=Optional.ofNullable(Files.probeContentType(r.getFile().toPath())).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);return ResponseEntity.ok().contentType(MediaType.parseMediaType(type)).body(r);}catch(Exception e){return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(r);}}

    private List<Map<String,Object>> parseRows(String raw){try{return mapper.readValue(raw==null?"[]":raw,new TypeReference<>(){});}catch(Exception e){throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid homework rows");}}
    private static String school(Authentication a){return String.valueOf(((Claims)a.getDetails()).get("schoolId"));}
    private static String staff(Authentication a){return String.valueOf(((Claims)a.getDetails()).get("staffUid"));}
    private static String text(Map<String,Object> body,String key){Object v=body.get(key);return v==null?"":String.valueOf(v);}
    private static Map<String,Object> map(Object... values){Map<String,Object> out=new LinkedHashMap<>();for(int i=0;i+1<values.length;i+=2)out.put(String.valueOf(values[i]),values[i+1]);return out;}
}
