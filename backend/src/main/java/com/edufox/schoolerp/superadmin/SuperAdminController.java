package com.edufox.schoolerp.superadmin;

import com.edufox.schoolerp.auth.AuthDtos;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {
    private final SuperAdminService service;

    @PostMapping("/login")
    public AuthDtos.LoginResponse login(@Valid @RequestBody SuperAdminDtos.LoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/setup/bootstrap")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Map<String, Object> bootstrap() { return service.bootstrap(); }

    @GetMapping("/setup/cities")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public java.util.List<Map<String, Object>> cities(@RequestParam(required = false) Integer stateId) {
        return service.cities(stateId);
    }

    @GetMapping("/page-mapping/bootstrap")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Map<String, Object> pageMappingBootstrap() { return service.pageMappingBootstrap(); }

    @GetMapping("/page-mapping/modules")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public java.util.List<Map<String, Object>> pageMappingModules(@RequestParam String schoolId) {
        return service.pageMappingModules(schoolId);
    }

    @GetMapping("/page-mapping/pages")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public java.util.List<Map<String, Object>> pageMappingPages(@RequestParam String schoolId,
                                                                 @RequestParam int moduleId) {
        return service.pageMappingPages(schoolId, moduleId);
    }

    @PostMapping("/page-mapping")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Map<String, Object> savePageMappings(@Valid @RequestBody SuperAdminDtos.PageMappingSaveRequest request) {
        return service.savePageMappings(request);
    }

    @GetMapping("/setup/schools/{schoolId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Map<String, Object> school(@PathVariable String schoolId) { return service.school(schoolId); }

    @PostMapping("/setup/schools")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Map<String, Object> saveSchool(@Valid @RequestBody SuperAdminDtos.SchoolRequest request) {
        return service.saveSchool(request);
    }

    @PostMapping("/setup/schools/{schoolId}/modules")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Map<String, Object> saveModules(@PathVariable String schoolId, @RequestBody SuperAdminDtos.SelectionRequest request) {
        return service.saveModules(schoolId, request);
    }

    @PostMapping("/setup/schools/{schoolId}/roles")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Map<String, Object> saveRoles(@PathVariable String schoolId, @RequestBody SuperAdminDtos.SelectionRequest request) {
        return service.saveRoles(schoolId, request);
    }
}
