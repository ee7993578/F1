package com.edufox.schoolerp.communication;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class CommunicationController {
    private final CommunicationService service;

    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(@RequestParam(defaultValue="") String session, Authentication a) { return service.bootstrap(school(a), session); }
    @GetMapping("/students") public List<Map<String,Object>> students(@RequestParam String session,@RequestParam(defaultValue="") String classIds,@RequestParam(defaultValue="0") String studentType,Authentication a) { return service.students(school(a),session,classIds,studentType); }
    @GetMapping("/faculty") public List<Map<String,Object>> faculty(Authentication a) { return service.faculty(school(a)); }
    @GetMapping("/fee-students") public List<Map<String,Object>> feeStudents(@RequestParam String session,@RequestParam(defaultValue="0") int feeType,@RequestParam(defaultValue="0") int installment,@RequestParam(defaultValue="2") int defaulterType,@RequestParam(defaultValue="0") String classId,Authentication a) { return service.feeStudents(school(a),session,feeType,installment,defaulterType,classId); }
    @PostMapping("/sms/send") public Map<String,Object> sendSms(@RequestBody Map<String,Object> body,Authentication a) { return service.sendGeneral(school(a),staff(a),body); }
    @PostMapping("/fee-sms/send") public Map<String,Object> sendFeeSms(@RequestBody Map<String,Object> body,Authentication a) { return service.sendFeeSms(school(a),staff(a),body); }
    @GetMapping("/reports/email") public List<Map<String,Object>> emailReport(@RequestParam Map<String,String> query,Authentication a) { return service.emailReport(school(a),query); }
    @GetMapping("/reports/sms") public List<Map<String,Object>> smsReport(@RequestParam Map<String,String> query,Authentication a) { return service.smsReport(school(a),query); }
    @GetMapping("/fee-notice") public List<Map<String,Object>> feeNotice(@RequestParam Map<String,String> query,Authentication a) { return service.feeNotice(school(a),query); }
    @PostMapping("/fee-notice/email") public Map<String,Object> feeNoticeEmail(@RequestBody Map<String,Object> body,Authentication a) { return service.queueFeeNoticeEmails(school(a),staff(a),body); }

    private static Claims claims(Authentication a) { return (Claims) a.getDetails(); }
    private static String school(Authentication a) { return text(claims(a).get("schoolId")); }
    private static String staff(Authentication a) { return text(claims(a).get("staffUid")); }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
}
