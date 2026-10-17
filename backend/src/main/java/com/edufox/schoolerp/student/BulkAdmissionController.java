package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.edufox.schoolerp.config.DatabaseWriteGuard;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students/bulk-uploads")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class BulkAdmissionController {
    private final BulkAdmissionRepository repository;
    private final DatabaseWriteGuard writeGuard;

    @GetMapping
    public List<Map<String, Object>> history(@RequestParam String session, Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return repository.history(String.valueOf(claims.get("schoolId")), session);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> upload(@RequestParam String session, @RequestPart MultipartFile file,
                                      Authentication authentication) throws Exception {
        writeGuard.requireWritable();
        Claims claims = (Claims) authentication.getDetails();
        if (file.isEmpty()) throw new IllegalArgumentException("Select an Excel file");
        if (!file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) throw new IllegalArgumentException("Only Excel files (.xlsx) are allowed");
        return repository.upload(String.valueOf(claims.get("schoolId")), String.valueOf(claims.get("staffUid")), session, file.getInputStream());
    }

    @DeleteMapping("/{excelId}")
    public Map<String, Object> delete(@PathVariable String excelId, @RequestParam String session, Authentication authentication) {
        writeGuard.requireWritable();
        Claims claims = (Claims) authentication.getDetails();
        int count = repository.delete(String.valueOf(claims.get("schoolId")), session, excelId);
        return Map.of("deleted", count, "message", count > 0 ? "Excel Data Deleted Successfully" : "Something went wrong while deleting data");
    }
}
