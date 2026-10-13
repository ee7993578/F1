package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student-settings/exam-configuration")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class ExamConfigurationController {
    private final ExamConfigurationService service;
    @GetMapping public List<Map<String,Object>> list(@RequestParam(required=false) String session,Authentication a){Claims c=(Claims)a.getDetails();return service.list(String.valueOf(c.get("schoolId")),session);}
    @PostMapping public ExamConfigurationDtos.ExamResponse save(@RequestBody ExamConfigurationDtos.ExamRequest r,Authentication a){Claims c=(Claims)a.getDetails();return service.save(String.valueOf(c.get("schoolId")),String.valueOf(c.get("staffUid")),r);}
    @PutMapping("/{id}") public ExamConfigurationDtos.ExamResponse update(@PathVariable Integer id,@RequestBody ExamConfigurationDtos.ExamRequest r,Authentication a){Claims c=(Claims)a.getDetails();return service.save(String.valueOf(c.get("schoolId")),String.valueOf(c.get("staffUid")),new ExamConfigurationDtos.ExamRequest(id,r.classId(),r.examDate(),r.examReportingTime(),r.examEntryGateNO(),r.examStartTime(),r.examEndTime(),r.examSession(),r.studentType()));}
    @DeleteMapping("/{id}") public ExamConfigurationDtos.ExamResponse delete(@PathVariable Integer id,Authentication a){Claims c=(Claims)a.getDetails();return service.delete(String.valueOf(c.get("schoolId")),id);}
}