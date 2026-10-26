package com.edufox.schoolerp.leave.work.leave;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRepository repository;

    public Map<String, Object> bootstrap(String school, String staff) { return repository.bootstrap(school, staff); }
    public List<Map<String, Object>> details(String school, String staff, String session) { return repository.facultyLeaveDetails(school, staff, session); }
    public List<Map<String, Object>> applications(String school, String staff, String session, String status, boolean principal, boolean admin) { return principal ? repository.applicationsForPrincipal(school, session, staff, status) : repository.applicationsForHead(school, session, staff, status, admin); }
    public Map<String, Object> saveType(String school, Map<String, Object> body) { return repository.saveLeaveType(school, text(body, "leaveType"), text(body, "leaveTypeDisplay")); }

    @Transactional
    public Map<String, Object> allocate(String school, String staff, Map<String, Object> body) {
        repository.saveAllocations(school, text(body, "staffUid"), text(body, "session"), staff, body.get("rows") instanceof List<?> list ? castRows(list) : List.of());
        return Map.of("message", "Record Saved Successfully");
    }

    @Transactional
    public Map<String, Object> apply(String school, String staff, Map<String, Object> body, MultipartFile file) throws IOException {
        String path = "";
        if (file != null && !file.isEmpty()) {
            String original = Optional.ofNullable(file.getOriginalFilename()).orElse("medical-certificate").replaceAll("[^A-Za-z0-9._-]", "_");
            String name = UUID.randomUUID().toString().replace("-", "") + "_" + original;
            Path directory = Path.of("uploads_doc", "medicalcertificate_leave");
            Files.createDirectories(directory);
            Files.copy(file.getInputStream(), directory.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            path = "~/Uploads_doc/medicalcertificate_leave/" + name;
        }
        repository.createApplication(body, school, staff, text(body, "session"), path);
        return Map.of("message", "Record Saved Successfully");
    }

    public Map<String, Object> headStatus(String school, String staff, String applicationId, String status, String remark) { return repository.updateHeadStatus(school, applicationId, staff, status, remark); }
    public Map<String, Object> principalStatus(String school, String staff, String applicationId, String status, String remark) { return repository.updatePrincipalStatus(school, applicationId, staff, status, remark); }

    private static String text(Map<String, Object> body, String key) { return body.get(key) == null ? "" : String.valueOf(body.get(key)).trim(); }
    private static List<Map<String, Object>> castRows(List<?> rows) { List<Map<String, Object>> result = new ArrayList<>(); for (Object row : rows) if (row instanceof Map<?, ?> map) { Map<String, Object> copy = new LinkedHashMap<>(); map.forEach((key, value) -> copy.put(String.valueOf(key), value)); result.add(copy); } return result; }
}
