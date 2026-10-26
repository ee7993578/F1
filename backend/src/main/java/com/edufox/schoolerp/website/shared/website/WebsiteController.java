package com.edufox.schoolerp.website.shared.website;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
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
@RequestMapping("/api/website")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class WebsiteController {
    private final WebsiteService service;

    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(Authentication a){return service.bootstrap(school(a));}
    @GetMapping("/students") public List<Map<String,Object>> students(@RequestParam String session,@RequestParam(defaultValue="") String sectionIds,Authentication a){return service.students(school(a),session,sectionIds);}
    @GetMapping("/exam-options") public List<Map<String,Object>> examOptions(@RequestParam String session,@RequestParam String classId,Authentication a){return service.exams(school(a),session,classId);}

    @GetMapping("/notices") public List<Map<String,Object>> notices(@RequestParam String session,@RequestParam(defaultValue="") String type,Authentication a){return service.notices(school(a),session,type);}
    @PostMapping(value="/notices",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveNotice(@RequestParam String session,@RequestParam String type,@RequestParam(defaultValue="") String category,@RequestParam(defaultValue="") String noticeDate,@RequestParam(defaultValue="") String description,@RequestParam(defaultValue="") String classIds,@RequestParam(defaultValue="") String studentIds,@RequestPart(value="file",required=false) MultipartFile file,Authentication a){return service.saveNotice(school(a),staff(a),session,type,category,noticeDate,description,classIds,studentIds,file);}
    @DeleteMapping("/notices/{id}") public Map<String,Object> deleteNotice(@PathVariable String id,Authentication a){service.deleteNotice(school(a),id);return msg("Record deleted successfully");}

    @GetMapping("/news") public List<Map<String,Object>> news(Authentication a){return service.news(school(a));}
    @PostMapping(value="/news",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveNews(@RequestParam String session,@RequestParam(defaultValue="") String title,@RequestParam(defaultValue="") String link,@RequestParam(defaultValue="") String description,@RequestParam(defaultValue="") String date,@RequestPart(value="files",required=false) List<MultipartFile> files,Authentication a){return service.saveNews(school(a),staff(a),session,title,link,description,date,files);}
    @DeleteMapping("/news/{id}") public Map<String,Object> deleteNews(@PathVariable String id,Authentication a){service.deleteNews(school(a),id);return msg("Record deleted successfully");}

    @GetMapping("/exam-schedules") public List<Map<String,Object>> examSchedules(@RequestParam String session,Authentication a){return service.examSchedules(school(a),session);}
    @PostMapping(value="/exam-schedules",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveExamSchedule(@RequestParam String session,@RequestParam String classId,@RequestParam String examTypeId,@RequestParam String title,@RequestPart("file") MultipartFile file,Authentication a){return service.saveExamSchedule(school(a),staff(a),session,classId,examTypeId,title,file);}
    @DeleteMapping("/exam-schedules/{id}") public Map<String,Object> deleteExamSchedule(@PathVariable String id,Authentication a){service.deleteExamSchedule(school(a),id);return msg("Record Deleted Successfully");}

    @GetMapping("/galleries") public List<Map<String,Object>> galleries(@RequestParam String session,Authentication a){return service.galleries(school(a),session);}
    @PostMapping(value="/galleries",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveGallery(@RequestParam String session,@RequestParam String title,@RequestParam String date,@RequestParam(defaultValue="") String description,@RequestParam(defaultValue="") String driveLink,@RequestPart(value="files",required=false) List<MultipartFile> files,Authentication a){return service.saveGallery(school(a),staff(a),session,title,date,description,driveLink,files);}
    @PutMapping(value="/galleries",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> updateGallery(@RequestParam String id,@RequestParam String session,@RequestParam String title,@RequestParam String date,@RequestPart(value="files",required=false) List<MultipartFile> files,Authentication a){return service.updateGallery(school(a),staff(a),session,id,title,date,files);}
    @DeleteMapping("/galleries/{id}") public Map<String,Object> deleteGallery(@PathVariable String id,@RequestParam String session,Authentication a){service.deleteGallery(school(a),session,id);return msg("Gallery deleted successfully");}

    @GetMapping("/videos") public List<Map<String,Object>> videos(@RequestParam String session,Authentication a){return service.videos(school(a),session);}
    @PostMapping("/videos") public Map<String,Object> saveVideo(@RequestBody Map<String,Object> body,Authentication a){return service.saveVideo(school(a),staff(a),text(body,"session"),text(body,"title"),text(body,"date"),text(body,"description"),text(body,"videoUrl"));}
    @DeleteMapping("/videos/{id}") public Map<String,Object> deleteVideo(@PathVariable String id,Authentication a){service.deleteVideo(school(a),id);return msg("Video Deleted Successfully");}

    @GetMapping("/events") public List<Map<String,Object>> events(@RequestParam String session,Authentication a){return service.events(school(a),session);}
    @PostMapping(value="/events",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveEvent(@RequestParam String session,@RequestParam String name,@RequestParam String date,@RequestParam(defaultValue="") String description,@RequestPart(value="files",required=false) List<MultipartFile> files,Authentication a){return service.saveEvent(school(a),staff(a),session,name,date,description,files);}
    @DeleteMapping("/events/{id}") public Map<String,Object> deleteEvent(@PathVariable String id,Authentication a){service.deleteEvent(school(a),id);return msg("Event deleted");}

    @GetMapping("/tc") public List<Map<String,Object>> tc(@RequestParam String session,Authentication a){return service.transferCertificates(school(a),session);}
    @GetMapping("/tc/student") public Map<String,Object> tcStudent(@RequestParam String session,@RequestParam String admissionNo,Authentication a){return service.withdrawnStudent(school(a),session,admissionNo);}
    @PostMapping(value="/tc",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String,Object> saveTc(@RequestParam String session,@RequestParam String admissionNo,@RequestPart("file") MultipartFile file,Authentication a){return service.saveTransferCertificate(school(a),staff(a),session,admissionNo,file);}

    @GetMapping("/files") public ResponseEntity<Resource> file(@RequestParam String path){Resource resource=service.file(path);try{MediaType media=MediaType.parseMediaType(Optional.ofNullable(Files.probeContentType(resource.getFile().toPath())).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE));return ResponseEntity.ok().contentType(media).body(resource);}catch(Exception e){return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);}}

    private static String school(Authentication a){return String.valueOf(((Claims)a.getDetails()).get("schoolId"));}
    private static String staff(Authentication a){return String.valueOf(((Claims)a.getDetails()).get("staffUid"));}
    private static String text(Map<String,Object> b,String k){Object v=b.get(k);return v==null?"":String.valueOf(v).trim();}
    private static Map<String,Object> msg(String value){return Map.of("message",value);}
}
