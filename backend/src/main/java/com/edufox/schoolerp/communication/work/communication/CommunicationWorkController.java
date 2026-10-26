package com.edufox.schoolerp.communication.work.communication;

import com.edufox.schoolerp.communication.shared.communication.CommunicationService;
import com.edufox.schoolerp.common.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Work pages and message actions for Communication. */
@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class CommunicationWorkController {
    private final CommunicationService service;

    private static String school(Authentication a) { return AuthContext.value(a, "schoolId"); }
    private static String staff(Authentication a) { return AuthContext.value(a, "staffUid"); }

    @GetMapping("/bootstrap") public Map<String, Object> bootstrap(@RequestParam(defaultValue = "") String session, Authentication a) { return service.bootstrap(school(a), session); }
    @GetMapping("/students") public List<Map<String, Object>> students(@RequestParam String session, @RequestParam(defaultValue = "") String classIds, @RequestParam(defaultValue = "0") String studentType, Authentication a) { return service.students(school(a), session, classIds, studentType); }
    @GetMapping("/faculty") public List<Map<String, Object>> faculty(Authentication a) { return service.faculty(school(a)); }
    @GetMapping("/fee-students") public List<Map<String, Object>> feeStudents(@RequestParam String session, @RequestParam(defaultValue = "0") int feeType, @RequestParam(defaultValue = "0") int installment, @RequestParam(defaultValue = "2") int defaulterType, @RequestParam(defaultValue = "0") String classId, Authentication a) { return service.feeStudents(school(a), session, feeType, installment, defaulterType, classId); }
    @PostMapping("/sms/send") public Map<String, Object> sendSms(@RequestBody Map<String, Object> body, Authentication a) { return service.sendGeneral(school(a), staff(a), body); }
    @PostMapping("/fee-sms/send") public Map<String, Object> sendFeeSms(@RequestBody Map<String, Object> body, Authentication a) { return service.sendFeeSms(school(a), staff(a), body); }
    @GetMapping("/fee-notice") public List<Map<String, Object>> feeNotice(@RequestParam Map<String, String> query, Authentication a) { return service.feeNotice(school(a), query); }
    @PostMapping("/fee-notice/email") public Map<String, Object> feeNoticeEmail(@RequestBody Map<String, Object> body, Authentication a) { return service.queueFeeNoticeEmails(school(a), staff(a), body); }
}
