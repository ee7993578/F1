package com.edufox.schoolerp.transport.report.transport_report;
import com.edufox.schoolerp.transport.work.student_transport.*;
import com.edufox.schoolerp.transport.work.transport.*;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transport/report-pages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class TransportReportController {
    private final TransportReportService service;
    private static String school(Authentication auth) { return String.valueOf(((Claims) auth.getDetails()).get("schoolId")); }

    @GetMapping("/bootstrap") public Map<String,Object> bootstrap(@RequestParam String session, Authentication auth) { return service.bootstrap(school(auth), session); }
    @GetMapping("/assign-route") public List<Map<String,Object>> assignRoute(@RequestParam String session, @RequestParam(defaultValue="0") String classId, @RequestParam(defaultValue="0") String routeId, Authentication auth) { return service.assignedRoute(school(auth), session, classId, routeId); }
    @GetMapping("/student") public List<Map<String,Object>> student(@RequestParam String session, @RequestParam(defaultValue="") String classIds, @RequestParam(defaultValue="") String routeIds, @RequestParam(defaultValue="0") String slabId, Authentication auth) { return service.student(school(auth), session, classIds, routeIds, slabId); }
    @GetMapping("/private-students") public List<Map<String,Object>> privateStudents(@RequestParam String session, @RequestParam(defaultValue="") String classIds, Authentication auth) { return service.privateStudents(school(auth), session, classIds); }
    @GetMapping("/monthwise") public Map<String,Object> monthwise(@RequestParam String session, @RequestParam(defaultValue="") String classIds, @RequestParam(defaultValue="0") String monthId, @RequestParam(defaultValue="0") String routeId, @RequestParam(defaultValue="0") String studentStatus, @RequestParam(defaultValue="0") String receiveAmount, Authentication auth) { return service.monthwise(school(auth), session, classIds, monthId, routeId, studentStatus, receiveAmount); }
    @GetMapping("/route-collection") public List<Map<String,Object>> routeCollection(@RequestParam String session, @RequestParam(defaultValue="0") String routeId, @RequestParam(defaultValue="0") String installment, @RequestParam(defaultValue="0") String classId, @RequestParam(defaultValue="0") String category, Authentication auth) { return service.routeCollection(school(auth), session, routeId, installment, classId, category); }
    @GetMapping("/route-collection-summary") public List<Map<String,Object>> routeCollectionSummary(@RequestParam String session, @RequestParam(defaultValue="0") String installment, Authentication auth) { return service.routeCollectionSummary(school(auth), session, installment); }
}
