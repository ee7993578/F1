package com.edufox.schoolerp.student.work.bulk_admission;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

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

    @GetMapping("/{excelId}/rows")
    public List<Map<String, Object>> rows(@PathVariable String excelId, @RequestParam String session,
                                          Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return repository.excelRows(String.valueOf(claims.get("schoolId")), session, excelId);
    }

    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestBody Map<String, Object> request, Authentication authentication) {
        writeGuard.requireWritable();
        Claims claims = (Claims) authentication.getDetails();
        return repository.confirm(String.valueOf(claims.get("schoolId")), String.valueOf(claims.get("staffUid")), request);
    }
}
