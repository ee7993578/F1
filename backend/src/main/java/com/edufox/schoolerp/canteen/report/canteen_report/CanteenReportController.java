package com.edufox.schoolerp.canteen.report.canteen_report;

import com.edufox.schoolerp.canteen.shared.canteen.CanteenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Read-only report pages for the Canteen module. */
@RestController
@RequestMapping("/api/canteen")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class CanteenReportController {
    private final CanteenService service;

    private static String school(Authentication a) { return String.valueOf(((Claims) a.getDetails()).get("schoolId")); }

    @GetMapping("/reports/debit-credit") public Map<String, Object> debitCredit(@RequestParam String session, @RequestParam(defaultValue = "0") String classId, @RequestParam(defaultValue = "0") String secId, Authentication a) { return service.debitCredit(school(a), session, classId, secId); }
    @GetMapping("/reports/invoice") public Map<String, Object> invoice(@RequestParam String session, @RequestParam String registrationNo, @RequestParam(defaultValue = "0") String classId, @RequestParam(defaultValue = "0") String secId, Authentication a) { return service.invoice(school(a), session, registrationNo, classId, secId); }
    @GetMapping("/reports/product-balance") public List<Map<String, Object>> productBalance(@RequestParam String session, Authentication a) { return service.productBalance(school(a), session); }
    @GetMapping("/reports/remaining-products") public List<Map<String, Object>> remaining(@RequestParam String session, Authentication a) { return service.remaining(school(a), session); }
    @GetMapping("/reports/assign-expenses") public List<Map<String, Object>> assignReport(@RequestParam String session, Authentication a) { return service.assignReport(school(a), session); }
    @GetMapping("/reports/product-issues") public List<Map<String, Object>> issues(@RequestParam String session, @RequestParam(defaultValue = "0") String userType, @RequestParam(defaultValue = "") String registrationNo, Authentication a) { return service.issues(school(a), session, userType, registrationNo); }
    @GetMapping("/reports/product-report") public List<Map<String, Object>> productReport(@RequestParam String session, @RequestParam(defaultValue = "0") String userType, Authentication a) { return service.productReport(school(a), session, userType); }
}
