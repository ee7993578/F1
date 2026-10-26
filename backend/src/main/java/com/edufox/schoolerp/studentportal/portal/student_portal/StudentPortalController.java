package com.edufox.schoolerp.studentportal.portal.student_portal;

import com.edufox.schoolerp.website.shared.website.WebsiteService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.util.*;

@RestController
@RequestMapping("/api/student-portal")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STUDENT','PARENT')")
public class StudentPortalController {
    private final StudentPortalService service;
    private final WebsiteService websiteService;

    private static Claims claims(Authentication a) { return (Claims) a.getDetails(); }
    private static String claim(Authentication a, String key) { return String.valueOf(claims(a).getOrDefault(key, "")); }
    private static String p(String value) { return value == null ? "" : value.trim(); }
    private String school(Authentication a) { return claim(a, "schoolId"); }
    private String role(Authentication a) { return claim(a, "role"); }
    private StudentPortalRepository.StudentContext context(Authentication a, String session, String studentId) {
        String school = school(a); String selectedSession = service.session(school, session);
        return service.context(school, selectedSession, claim(a, "staffUid"), claims(a).getSubject(), role(a), studentId);
    }

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap(@RequestParam(defaultValue="") String session, @RequestParam(defaultValue="") String studentId, Authentication a) {
        return service.bootstrap(school(a), claim(a, "staffUid"), claims(a).getSubject(), role(a), session, studentId);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(@RequestParam(defaultValue="") String session, @RequestParam(defaultValue="") String studentId, Authentication a) {
        return service.bootstrap(school(a), claim(a, "staffUid"), claims(a).getSubject(), role(a), session, studentId);
    }

    @GetMapping("/students")
    public Map<String, Object> students(@RequestParam(defaultValue="") String session, Authentication a) {
        return Map.of("students", service.students(school(a), session, claim(a, "staffUid"), claims(a).getSubject(), role(a)));
    }

    @GetMapping("/syllabus") public Map<String,Object> syllabus(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,Authentication a){return service.page(school(a),context(a,session,studentId),"syllabus");}
    @GetMapping("/circulars") public Map<String,Object> circulars(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,Authentication a){return service.page(school(a),context(a,session,studentId),"circulars");}
    @GetMapping("/holiday-homework") public Map<String,Object> holidayHomework(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,Authentication a){return service.page(school(a),context(a,session,studentId),"holiday-homework");}
    @GetMapping("/assignments") public Map<String,Object> assignments(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,Authentication a){return service.page(school(a),context(a,session,studentId),"assignments");}
    @GetMapping("/fees") public Map<String,Object> fees(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,Authentication a){return service.page(school(a),context(a,session,studentId),"fees");}
    @GetMapping("/attendance") public Map<String,Object> attendance(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,Authentication a){return service.page(school(a),context(a,session,studentId),"attendance");}
    @GetMapping("/timetable") public Map<String,Object> timetable(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,Authentication a){return service.page(school(a),context(a,session,studentId),"timetable");}
    @GetMapping("/exam-schedule") public Map<String,Object> examSchedule(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,Authentication a){return service.page(school(a),context(a,session,studentId),"exam-schedule");}
    @GetMapping("/academic-performance") public Map<String,Object> academicPerformance(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,@RequestParam(defaultValue="") String termId,Authentication a){var c=context(a,session,studentId);return Map.of("rows",service.academicPerformance(school(a),c,termId));}
    @GetMapping("/daily-homework") public Map<String,Object> dailyHomework(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,@RequestParam(defaultValue="") String date,Authentication a){var c=context(a,session,studentId);return Map.of("rows",service.dailyHomework(school(a),c,date));}
    @GetMapping("/attendance/detail") public Map<String,Object> attendanceDetail(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,@RequestParam String monthId,Authentication a){var c=context(a,session,studentId);return Map.of("rows",service.attendanceDetail(school(a),c,monthId));}
    @GetMapping("/payment-url") public Map<String,Object> paymentUrl(@RequestParam(defaultValue="") String session,@RequestParam(defaultValue="") String studentId,@RequestParam String installmentNo,Authentication a){var c=context(a,session,studentId);return service.paymentUrl(school(a),c,installmentNo);}

    /** Student-safe equivalent of the old file preview/download link. */
    @GetMapping("/file")
    public ResponseEntity<Resource> file(@RequestParam String path) {
        Resource resource = websiteService.file(path);
        try {
            String type = Optional.ofNullable(Files.probeContentType(resource.getFile().toPath())).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(type)).body(resource);
        } catch (Exception ignored) { return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource); }
    }
}
