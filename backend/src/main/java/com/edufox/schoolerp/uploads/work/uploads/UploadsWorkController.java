package com.edufox.schoolerp.uploads.work.uploads;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.edufox.schoolerp.common.auth.AuthContext;
import com.edufox.schoolerp.uploads.shared.uploads.UploadsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/** Work pages for planners, assignments, homework, holiday homework and syllabus. */
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class UploadsWorkController {
    private final UploadsService service;
    private final ObjectMapper mapper;

    private static String school(Authentication a) { return AuthContext.value(a, "schoolId"); }
    private static String staff(Authentication a) { return AuthContext.value(a, "staffUid"); }
    private static Map<String, Object> map(Object... values) { Map<String, Object> out = new LinkedHashMap<>(); for (int i = 0; i + 1 < values.length; i += 2) out.put(String.valueOf(values[i]), values[i + 1]); return out; }

    @GetMapping("/bootstrap") public Map<String, Object> bootstrap(@RequestParam(defaultValue = "") String session, Authentication a) { return service.bootstrap(school(a), session, staff(a)); }
    @GetMapping("/sections") public List<Map<String, Object>> sections(@RequestParam String session, @RequestParam String classId, Authentication a) { return service.sections(school(a), session, classId); }
    @GetMapping("/subjects") public List<Map<String, Object>> subjects(@RequestParam String session, @RequestParam String secId, @RequestParam String classId, Authentication a) { return service.subjects(school(a), session, secId, classId); }
    @GetMapping("/students") public List<Map<String, Object>> students(@RequestParam String session, @RequestParam String secId, Authentication a) { return service.students(school(a), session, secId); }
    @GetMapping("/terms") public List<Map<String, Object>> terms(@RequestParam String session, @RequestParam String classId, Authentication a) { return service.terms(school(a), session, classId); }
    @GetMapping("/exams") public List<Map<String, Object>> exams(@RequestParam String session, @RequestParam String classId, @RequestParam String termId, Authentication a) { return service.exams(school(a), session, classId, termId); }

    @GetMapping("/planners") public List<Map<String, Object>> planners(@RequestParam String session, Authentication a) { return service.planners(school(a), session); }
    @PostMapping(value = "/planners", consumes = "multipart/form-data") public Map<String, Object> savePlanner(@RequestParam String session, @RequestParam String title, @RequestPart("file") MultipartFile file, Authentication a) { return service.savePlanner(school(a), staff(a), session, title, file); }
    @PutMapping(value = "/planners/{id}", consumes = "multipart/form-data") public Map<String, Object> updatePlanner(@PathVariable String id, @RequestParam String session, @RequestParam String title, @RequestPart(value = "file", required = false) MultipartFile file, Authentication a) { return service.updatePlanner(school(a), staff(a), session, id, title, file); }
    @DeleteMapping("/planners/{id}") public Map<String, Object> deletePlanner(@PathVariable String id, Authentication a) { return service.deletePlanner(school(a), id); }

    @GetMapping("/assignments") public List<Map<String, Object>> assignments(@RequestParam String session, Authentication a) { return service.assignments(school(a), session, staff(a)); }
    @PostMapping(value = "/assignments", consumes = "multipart/form-data") public Map<String, Object> saveAssignment(@RequestParam String session, @RequestParam String secId, @RequestParam String classId, @RequestParam String className, @RequestParam String subjectId, @RequestParam String studentIds, @RequestParam String postedDate, @RequestParam String submissionDate, @RequestParam String message, @RequestPart(value = "file", required = false) MultipartFile file, Authentication a) { return service.saveAssignment(school(a), staff(a), session, secId, classId, className, subjectId, studentIds, postedDate, submissionDate, message, file); }
    @DeleteMapping("/assignments/{assignId}") public Map<String, Object> deleteAssignment(@PathVariable String assignId, Authentication a) { return service.deleteAssignment(school(a), assignId); }

    @GetMapping("/daily-homework/view") public List<Map<String, Object>> dailyView(@RequestParam String session, @RequestParam String secId, @RequestParam String date, Authentication a) { return service.dailyHomeworkView(school(a), session, secId, date); }
    @GetMapping("/daily-homework/subjects") public List<Map<String, Object>> dailySubjects(@RequestParam String session, @RequestParam String secId, @RequestParam String classId, @RequestParam String date, Authentication a) { return service.dailyHomeworkSubjects(school(a), session, secId, classId, date); }
    @PostMapping(value = "/daily-homework", consumes = "multipart/form-data") public Map<String, Object> saveDaily(@RequestParam String session, @RequestParam String secId, @RequestParam String date, @RequestParam String rows, @RequestPart(value = "files", required = false) List<MultipartFile> files, Authentication a) { return service.saveDailyHomework(school(a), staff(a), session, secId, date, parseRows(rows), files); }
    @DeleteMapping("/daily-homework/{id}") public Map<String, Object> deleteDaily(@PathVariable String id, Authentication a) { return service.deleteDailyHomework(school(a), id); }

    @GetMapping("/holiday-homework") public Map<String, Object> holiday(@RequestParam String session, Authentication a) { return map("rows", service.holidays(school(a), session), "linkTitle", service.holidayLink(school(a), session)); }
    @PostMapping(value = "/holiday-homework", consumes = "multipart/form-data") public Map<String, Object> saveHoliday(@RequestParam String session, @RequestParam String classId, @RequestParam String sectionIds, @RequestParam(defaultValue = "") String linkTitle, @RequestParam String title, @RequestParam(defaultValue = "") String submissionDate, @RequestPart("file") MultipartFile file, Authentication a) { return service.saveHoliday(school(a), staff(a), session, classId, sectionIds, linkTitle, title, submissionDate, file); }
    @DeleteMapping("/holiday-homework/{titleId}") public Map<String, Object> deleteHoliday(@PathVariable String titleId, @RequestParam String session, Authentication a) { return service.deleteHoliday(school(a), session, titleId, staff(a)); }

    @GetMapping("/syllabus") public List<Map<String, Object>> syllabus(@RequestParam String session, Authentication a) { return service.syllabus(school(a), session); }
    @PostMapping(value = "/syllabus", consumes = "multipart/form-data") public Map<String, Object> saveSyllabus(@RequestParam String session, @RequestParam String title, @RequestParam String classId, @RequestParam(defaultValue = "0") String secId, @RequestParam String termId, @RequestParam String examId, @RequestPart("file") MultipartFile file, Authentication a) { return service.saveSyllabus(school(a), staff(a), session, title, classId, secId, termId, examId, file); }
    @PutMapping(value = "/syllabus/{id}", consumes = "multipart/form-data") public Map<String, Object> updateSyllabus(@PathVariable String id, @RequestParam String session, @RequestParam String title, @RequestParam String classId, @RequestParam(defaultValue = "0") String secId, @RequestParam String termId, @RequestParam String examId, @RequestPart(value = "file", required = false) MultipartFile file, Authentication a) { return service.updateSyllabus(school(a), staff(a), session, id, title, classId, secId, termId, examId, file); }
    @DeleteMapping("/syllabus/{id}") public Map<String, Object> deleteSyllabus(@PathVariable String id, @RequestParam String session, Authentication a) { return service.deleteSyllabus(school(a), session, id, staff(a)); }

    private List<Map<String, Object>> parseRows(String raw) { try { return mapper.readValue(raw == null ? "[]" : raw, new TypeReference<>() {}); } catch (Exception e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid homework rows"); } }
}
