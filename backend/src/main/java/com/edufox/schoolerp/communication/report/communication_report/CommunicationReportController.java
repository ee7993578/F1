package com.edufox.schoolerp.communication.report.communication_report;

import com.edufox.schoolerp.communication.shared.communication.CommunicationService;
import com.edufox.schoolerp.common.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Communication email/SMS report pages. */
@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class CommunicationReportController {
    private final CommunicationService service;

    private static String school(Authentication a) { return AuthContext.value(a, "schoolId"); }

    @GetMapping("/reports/email") public List<Map<String, Object>> emailReport(@RequestParam Map<String, String> query, Authentication a) { return service.emailReport(school(a), query); }
    @GetMapping("/reports/sms") public List<Map<String, Object>> smsReport(@RequestParam Map<String, String> query, Authentication a) { return service.smsReport(school(a), query); }
}
