package com.edufox.schoolerp.timetable.work.timetable;
import com.edufox.schoolerp.timetable.setup.virtual_class.*;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class TimetableController {
    private final TimetableService service;

    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(@RequestParam(required=false, defaultValue="") String session, Authentication a) { return service.bootstrap(school(claims(a)), session); }
    @GetMapping("/assignment") public Object assignment(@RequestParam String session, @RequestParam(defaultValue="0") int sectionId, @RequestParam(defaultValue="0") int facultyId, @RequestParam String day, @RequestParam String ttUid, Authentication a) { return service.assignment(school(claims(a)), session, sectionId, facultyId, day, ttUid); }
    @GetMapping("/subjects") public Object subjects(@RequestParam String session, @RequestParam int sectionId, Authentication a) { return service.subjects(school(claims(a)), session, sectionId); }
    @PostMapping("/assignment") public Map<String,Object> assignmentSave(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("saved", service.saveAssignment(school(c), staff(c), b), "message", "Time Table Saved Successfully"); }
    @GetMapping("/alternate") public Map<String,Object> alternate(@RequestParam String session, @RequestParam int facultyId, @RequestParam String ttUid, Authentication a) { return service.alternate(school(claims(a)), session, facultyId, ttUid); }
    @PostMapping("/alternate") public Map<String,Object> alternateSave(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("saved", service.saveAlternate(school(c), staff(c), b), "message", "Alternate Faculty Saved Successfully"); }
    @GetMapping("/activity") public Map<String,Object> activity(@RequestParam String session, @RequestParam int classId, Authentication a) { return service.activity(school(claims(a)), session, classId); }
    @PostMapping("/activity") public Map<String,Object> activitySave(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("saved", service.saveActivity(school(c), staff(c), b), "message", "Activity Assigned Successfully"); }
    @GetMapping("/holidays") public Map<String,Object> holidays(@RequestParam String session, Authentication a) { return service.holidays(school(claims(a)), session); }
    @PostMapping("/holidays") public Map<String,Object> holidaySave(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("saved", service.saveHoliday(school(c), staff(c), b), "message", "Holiday Added Successfully"); }
    @DeleteMapping("/holidays") public Map<String,Object> holidayDelete(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("deleted", service.deleteHoliday(school(c), staff(c), b), "message", "Holiday Deleted Successfully"); }
    @GetMapping("/mapping") public Map<String,Object> mapping(@RequestParam String session, @RequestParam int facultyId, @RequestParam int sectionId, Authentication a) { return service.mapping(school(claims(a)), session, facultyId, sectionId); }
    @PostMapping("/mapping") public Map<String,Object> mappingSave(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("saved", service.saveMapping(school(c), staff(c), b), "message", "Faculty Subject Mapping Saved Successfully"); }
    @GetMapping("/virtual-class/options") public Object virtualClassOptions(Authentication a) { Claims c=claims(a); return service.virtualClassOptions(school(c),staff(c)); }
    @GetMapping("/virtual-class/students") public Object virtualClassStudents(@RequestParam int sectionId,@RequestParam int subjectId,Authentication a) { return service.virtualClassStudents(school(claims(a)),sectionId,subjectId); }
    @GetMapping("/virtual-class") public Map<String,Object> virtualClass(@RequestParam String session, @RequestParam(required=false, defaultValue="") String facultyId, @RequestParam(required=false, defaultValue="") String subjectId, @RequestParam(required=false, defaultValue="") String vClassId, Authentication a) { Map<String,Object> f=new LinkedHashMap<>(); f.put("facultyId", facultyId); f.put("subjectId", subjectId); f.put("vClassId", vClassId); Claims c=claims(a); return service.virtualClass(school(c),staff(c),session,f); }
    @PostMapping("/virtual-class") public Map<String,Object> virtualClassSave(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("saved", service.saveVirtualClass(school(c), staff(c), b), "message", "Virtual Class Saved Successfully"); }
    @DeleteMapping("/virtual-class") public Map<String,Object> virtualClassDelete(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("deleted", service.deleteVirtualClass(school(c), staff(c), b), "message", "Virtual Class Deleted Successfully"); }
    @PostMapping("/config") public Map<String,Object> configSave(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("saved", service.saveConfig(school(c), staff(c), b), "message", "Time Table Configuration Saved Successfully"); }
    @DeleteMapping("/config") public Map<String,Object> configDelete(@RequestBody Map<String,Object> b, Authentication a) { Claims c=claims(a); return Map.of("deleted", service.deleteConfig(school(c), staff(c), b), "message", "Time Table Configuration Deleted Successfully"); }
    @GetMapping("/report") public Map<String,Object> report(@RequestParam String session, @RequestParam String type, @RequestParam Map<String,String> params, Authentication a) { return service.report(school(claims(a)), session, type, params); }

    private static Claims claims(Authentication a) { if (a==null || !(a.getDetails() instanceof Claims c)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authenticated claims are missing"); return c; }
    private static String school(Claims c) { String v=c.get("schoolId",String.class); if(v==null||v.isBlank()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"School claim is missing"); return v; }
    private static String staff(Claims c) { String v=c.get("staffUid",String.class); return v==null?"":v; }
}
