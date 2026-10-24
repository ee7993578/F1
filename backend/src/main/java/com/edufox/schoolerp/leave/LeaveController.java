package com.edufox.schoolerp.leave;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class LeaveController {
    private final LeaveService service;
    private final ObjectMapper mapper;

    private static String claim(Authentication a, String key) { return String.valueOf(((Claims) a.getDetails()).get(key)); }
    private static String text(Map<String, Object> body, String key) { return body.get(key) == null ? "" : String.valueOf(body.get(key)).trim(); }

    @GetMapping("/bootstrap") public Map<String, Object> bootstrap(Authentication a) { return service.bootstrap(claim(a, "schoolId"), claim(a, "staffUid")); }
    @GetMapping("/details") public List<Map<String, Object>> details(@RequestParam String staffUid, @RequestParam String session, Authentication a) { return service.details(claim(a, "schoolId"), staffUid, session); }
    @GetMapping("/applications") public List<Map<String, Object>> applications(@RequestParam String session, @RequestParam(defaultValue = "0") String status, @RequestParam(defaultValue = "head") String level, Authentication a) { return service.applications(claim(a, "schoolId"), claim(a, "staffUid"), session, status, "principal".equalsIgnoreCase(level), "1".equals(String.valueOf(service.bootstrap(claim(a, "schoolId"), claim(a, "staffUid")).get("isAdmin")))); }
    @PostMapping("/types") public Map<String, Object> saveType(@RequestBody Map<String, Object> body, Authentication a) { return service.saveType(claim(a, "schoolId"), body); }
    @PostMapping("/allocation") public Map<String, Object> allocation(@RequestBody Map<String, Object> body, Authentication a) { return service.allocate(claim(a, "schoolId"), claim(a, "staffUid"), body); }
    @PostMapping(value = "/applications", consumes = MediaType.APPLICATION_JSON_VALUE) public Map<String, Object> applyJson(@RequestBody Map<String, Object> body, Authentication a) throws IOException { return service.apply(claim(a, "schoolId"), claim(a, "staffUid"), body, null); }
    @PostMapping(value = "/applications", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) public Map<String, Object> applyMultipart(@RequestPart("data") String data, @RequestPart(value = "medicalCertificate", required = false) MultipartFile file, Authentication a) throws IOException { Map<String, Object> body = mapper.readValue(data, new TypeReference<>() {}); return service.apply(claim(a, "schoolId"), claim(a, "staffUid"), body, file); }
    @PutMapping("/applications/{id}/head-status") public Map<String, Object> headStatus(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication a) { return service.headStatus(claim(a, "schoolId"), claim(a, "staffUid"), id, text(body, "status"), text(body, "remark")); }
    @PutMapping("/applications/{id}/principal-status") public Map<String, Object> principalStatus(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication a) { return service.principalStatus(claim(a, "schoolId"), claim(a, "staffUid"), id, text(body, "status"), text(body, "remark")); }
}
