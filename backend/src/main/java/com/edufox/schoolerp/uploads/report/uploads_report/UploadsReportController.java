package com.edufox.schoolerp.uploads.report.uploads_report;

import com.edufox.schoolerp.common.auth.AuthContext;
import com.edufox.schoolerp.uploads.shared.uploads.UploadsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.util.*;

/** Upload reports and uploaded-file download endpoint. */
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class UploadsReportController {
    private final UploadsService service;

    private static String school(Authentication a) { return AuthContext.value(a, "schoolId"); }
    private static String staff(Authentication a) { return AuthContext.value(a, "staffUid"); }
    private static Map<String, Object> map(Object... values) { Map<String, Object> out = new LinkedHashMap<>(); for (int i = 0; i + 1 < values.length; i += 2) out.put(String.valueOf(values[i]), values[i + 1]); return out; }

    @GetMapping("/reports/academic-planner") public List<Map<String, Object>> plannerReport(@RequestParam String session, Authentication a) { return service.plannerReport(school(a), session); }
    @GetMapping("/reports/assignment") public List<Map<String, Object>> assignmentReport(@RequestParam String session, Authentication a) { return service.assignmentReport(school(a), session, staff(a)); }
    @GetMapping("/reports/daily-homework") public Map<String, Object> dailyReport(@RequestParam String session, @RequestParam String secId, @RequestParam String date, Authentication a) { return map("rows", service.dailyReport(school(a), session, secId, date)); }
    @GetMapping("/reports/daily-homework/classes") public List<Map<String, Object>> dailyReportClasses(@RequestParam String session, Authentication a) { return service.dailyReportSections(school(a), session, staff(a)); }
    @GetMapping("/reports/mobile-coverage") public Map<String, Object> mobileReport(@RequestParam String session, @RequestParam(defaultValue = "detail") String mode, @RequestParam(defaultValue = "") String classId, @RequestParam(defaultValue = "") String secId, @RequestParam(defaultValue = "all") String status, Authentication a) { return service.mobileReport(school(a), session, mode, classId, secId, status); }
    @GetMapping("/reports/homework-dates") public List<Map<String, Object>> homeworkDates(@RequestParam String session, @RequestParam String secId, Authentication a) { return service.showHomeworkDates(school(a), session, secId); }
    @GetMapping("/reports/homework-details") public List<Map<String, Object>> homeworkDetails(@RequestParam String session, @RequestParam String secId, @RequestParam String date, Authentication a) { return service.showHomeworkDetails(school(a), session, secId, date); }
    @GetMapping("/reports/syllabus/classes") public List<Map<String, Object>> syllabusClasses(@RequestParam String session, Authentication a) { return service.showSyllabusClasses(school(a), session, staff(a)); }
    @GetMapping("/reports/syllabus/titles") public List<Map<String, Object>> syllabusTitles(@RequestParam String session, @RequestParam String classId, Authentication a) { return service.showSyllabusTitles(school(a), session, classId); }

    @GetMapping("/files") public ResponseEntity<Resource> file(@RequestParam String path) {
        Resource resource = service.file(path);
        try {
            String type = Optional.ofNullable(Files.probeContentType(resource.getFile().toPath())).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(type)).body(resource);
        } catch (Exception ignored) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        }
    }
}
