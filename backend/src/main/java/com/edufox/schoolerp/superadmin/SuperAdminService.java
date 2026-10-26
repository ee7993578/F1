package com.edufox.schoolerp.superadmin;

import com.edufox.schoolerp.auth.AuthDtos;
import com.edufox.schoolerp.auth.AuthUser;
import com.edufox.schoolerp.config.AppProperties;
import com.edufox.schoolerp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    private final AppProperties properties;
    private final JwtService jwt;
    private final SuperAdminRepository repository;

    public AuthDtos.LoginResponse login(SuperAdminDtos.LoginRequest request) {
        String configuredId = properties.getSuperAdmin().getIdentifier();
        String configuredPassword = properties.getSuperAdmin().getPassword();
        boolean validId = MessageDigest.isEqual(request.identifier().trim().getBytes(StandardCharsets.UTF_8), configuredId.getBytes(StandardCharsets.UTF_8));
        boolean validPassword = MessageDigest.isEqual(request.password().getBytes(StandardCharsets.UTF_8), configuredPassword.getBytes(StandardCharsets.UTF_8));
        if (!validId || !validPassword) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid superadmin credentials");

        AuthUser user = new AuthUser("__SUPERADMIN__", "Edufox Super Admin", "", "Super Admin",
            "SUPERADMIN", "", "", "", "SA", "SUPERADMIN", "", "Mobile", false);
        return new AuthDtos.LoginResponse(jwt.issueToken(user, "superadmin", request.identifier().trim()),
            "Bearer", properties.getAuth().getAccessTokenMinutes() * 60L, user.toDto(), false, "Superadmin login successful");
    }

    public Map<String, Object> bootstrap() { return repository.bootstrap(); }
    public java.util.List<Map<String, Object>> cities(Integer stateId) { return repository.cities(stateId); }
    public Map<String, Object> pageMappingBootstrap() { return repository.pageMappingBootstrap(); }
    public java.util.List<Map<String, Object>> pageMappingModules(String schoolId) { return repository.pageMappingModules(schoolId); }
    public java.util.List<Map<String, Object>> pageMappingPages(String schoolId, int moduleId) { return repository.pageMappingPages(schoolId, moduleId); }
    public Map<String, Object> savePageMappings(SuperAdminDtos.PageMappingSaveRequest request) { return repository.savePageMappings(request); }
    public Map<String, Object> school(String schoolId) { return repository.school(schoolId); }
    public Map<String, Object> saveSchool(SuperAdminDtos.SchoolRequest request) { return repository.saveSchool(request); }
    public Map<String, Object> saveModules(String schoolId, SuperAdminDtos.SelectionRequest request) { return repository.saveModules(schoolId, request); }
    public Map<String, Object> saveRoles(String schoolId, SuperAdminDtos.SelectionRequest request) { return repository.saveRoles(schoolId, request); }
}
