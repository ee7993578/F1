package com.edufox.schoolerp.master;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/master-settings")
@RequiredArgsConstructor
public class MasterSettingController {
    private final MasterSettingService service;

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap(Authentication authentication) {
        Claims claims = claims(authentication);
        return service.bootstrap(school(claims), staff(claims));
    }

    @GetMapping("/logs")
    public List<Map<String, Object>> logs(Authentication authentication,
                                          @RequestParam(required = false) String from,
                                          @RequestParam(required = false) String to,
                                          @RequestParam(required = false) String page,
                                          @RequestParam(required = false) String activity,
                                          @RequestParam(required = false) String staff) {
        return service.logs(school(claims(authentication)), from, to, page, activity, staff);
    }

    @GetMapping("/permissions/{designationId}")
    public List<Map<String, Object>> permissions(Authentication authentication,
                                                @PathVariable int designationId) {
        return service.permissions(school(claims(authentication)), designationId);
    }

    @GetMapping("/permissions/staff/{facultyId}")
    public List<Map<String, Object>> staffPermissions(Authentication authentication,
                                                       @PathVariable int facultyId,
                                                       @RequestParam Integer moduleId) {
        return service.staffPermissions(school(claims(authentication)), facultyId, moduleId);
    }

    @GetMapping("/module-mappings")
    public List<Map<String, Object>> moduleMappings(Authentication authentication,
                                                    @RequestParam(required = false) String session) {
        return service.moduleMappings(school(claims(authentication)), session);
    }

    @GetMapping("/schools")
    public List<Map<String, Object>> schools(Authentication authentication) {
        return service.schools(school(claims(authentication)));
    }

    @GetMapping("/module-page-mappings")
    public List<Map<String, Object>> modulePageMappings(Authentication authentication,
                                                         @RequestParam String schoolId,
                                                         @RequestParam Integer moduleId) {
        return service.modulePageMappings(school(claims(authentication)), schoolId, moduleId);
    }

    @PostMapping("/module-page-mappings")
    public Map<String, String> saveModulePageMappings(Authentication authentication,
                                                      @Valid @RequestBody MasterSettingDtos.ModulePageMappingRequest request) {
        Claims claims = claims(authentication);
        service.saveModulePageMappings(school(claims), staff(claims), request);
        return Map.of("message", "Page mapping saved");
    }

    @GetMapping("/staff-roles")
    public List<Map<String, Object>> staffRoles(Authentication authentication) {
        return service.staffRoles(school(claims(authentication)));
    }

    @GetMapping("/staff-roles/{facultyId}")
    public List<Integer> staffRoleIds(Authentication authentication, @PathVariable int facultyId) {
        return service.staffRoleIds(school(claims(authentication)), facultyId);
    }

    @PostMapping("/staff-roles")
    public Map<String, String> saveStaffRoles(Authentication authentication,
                                              @Valid @RequestBody MasterSettingDtos.StaffRoleSaveRequest request) {
        Claims claims = claims(authentication);
        service.saveStaffRoles(school(claims), staff(claims), request);
        return Map.of("message", "Designation assigned");
    }

    @GetMapping("/staff-roles/{facultyId}/pages")
    public List<Map<String, Object>> staffRolePages(Authentication authentication, @PathVariable int facultyId) {
        return service.staffRolePages(school(claims(authentication)), facultyId);
    }

    @PostMapping("/classes")
    public Map<String, String> createClass(Authentication authentication,
                                           @Valid @RequestBody MasterSettingDtos.ClassSaveRequest request) {
        Claims claims = claims(authentication);
        service.createClass(school(claims), staff(claims), request);
        return Map.of("message", "Class saved");
    }

    @PostMapping("/sections")
    public Map<String, String> createSections(Authentication authentication,
                                              @Valid @RequestBody MasterSettingDtos.ClassSaveRequest request) {
        Claims claims = claims(authentication);
        service.createSections(school(claims), staff(claims), request);
        return Map.of("message", "Section saved");
    }

    @PutMapping("/sections/{sectionId}")
    public Map<String, String> updateSection(Authentication authentication,
                                             @PathVariable int sectionId,
                                             @Valid @RequestBody MasterSettingDtos.SectionUpdateRequest request) {
        Claims claims = claims(authentication);
        service.updateSection(school(claims), staff(claims), sectionId, request);
        return Map.of("message", "Section updated");
    }

    @PostMapping("/designations")
    public Map<String, String> createDesignation(Authentication authentication,
                                                 @Valid @RequestBody MasterSettingDtos.DesignationCreateRequest request) {
        Claims claims = claims(authentication);
        service.createDesignation(school(claims), staff(claims), request);
        return Map.of("message", "Designation created");
    }

    @PutMapping("/designations/{id}")
    public Map<String, String> updateDesignation(Authentication authentication,
                                                 @PathVariable int id,
                                                 @Valid @RequestBody MasterSettingDtos.DesignationUpdateRequest request) {
        Claims claims = claims(authentication);
        service.updateDesignation(school(claims), staff(claims), id, request);
        return Map.of("message", "Designation updated");
    }

    @PostMapping("/sessions")
    public Map<String, String> createSession(Authentication authentication,
                                             @Valid @RequestBody MasterSettingDtos.SessionCreateRequest request) {
        Claims claims = claims(authentication);
        service.createSession(school(claims), staff(claims), request);
        return Map.of("message", "Session created");
    }

    @PostMapping("/sessions/{id}/default")
    public Map<String, String> setDefaultSession(Authentication authentication, @PathVariable int id) {
        service.setDefaultSession(school(claims(authentication)),
            new MasterSettingDtos.SessionDefaultRequest(id));
        return Map.of("message", "Default session updated");
    }

    @PostMapping("/terms")
    public Map<String, String> saveTerm(Authentication authentication,
                                        @Valid @RequestBody MasterSettingDtos.TermSaveRequest request) {
        Claims claims = claims(authentication);
        service.saveTerm(school(claims), staff(claims), request);
        return Map.of("message", "Session term saved");
    }

    @PostMapping("/module-mappings")
    public Map<String, String> saveModuleMapping(Authentication authentication,
                                                 @Valid @RequestBody MasterSettingDtos.ModuleMappingRequest request) {
        Claims claims = claims(authentication);
        service.saveModuleMapping(school(claims), staff(claims), request);
        return Map.of("message", "Module mapping saved");
    }

    @PutMapping("/permissions")
    public Map<String, String> savePermissions(Authentication authentication,
                                               @Valid @RequestBody MasterSettingDtos.PermissionSaveRequest request) {
        Claims claims = claims(authentication);
        service.savePermissions(school(claims), staff(claims), request);
        return Map.of("message", "Permissions saved");
    }

    @PutMapping("/permissions/staff")
    public Map<String, String> saveStaffPermissions(Authentication authentication,
                                                    @Valid @RequestBody MasterSettingDtos.StaffPermissionSaveRequest request) {
        Claims claims = claims(authentication);
        service.saveStaffPermissions(school(claims), staff(claims), request);
        return Map.of("message", "Permissions saved");
    }

    @GetMapping("/mobile-permissions/{designationId}")
    public List<Map<String, Object>> mobilePermissions(Authentication authentication,
                                                        @PathVariable int designationId) {
        return service.mobilePermissions(school(claims(authentication)), designationId);
    }

    @PutMapping("/mobile-permissions")
    public Map<String, String> saveMobilePermissions(Authentication authentication,
                                                     @Valid @RequestBody MasterSettingDtos.MobilePermissionSaveRequest request) {
        Claims claims = claims(authentication);
        service.saveMobilePermissions(school(claims), staff(claims), request);
        return Map.of("message", "Mobile App Pages Assigned");
    }

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {
        Claims claims = claims(authentication);
        return service.bootstrap(school(claims), staff(claims))
            .entrySet().stream()
            .filter(e -> e.getKey().equals("profile"))
            .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @PutMapping("/profile")
    public Map<String, String> updateProfile(Authentication authentication,
                                             @Valid @RequestBody MasterSettingDtos.ProfileUpdateRequest request) {
        Claims claims = claims(authentication);
        service.updateProfile(school(claims), staff(claims), request);
        return Map.of("message", "Profile updated");
    }

    @PostMapping("/profile/password")
    public Map<String, String> changePassword(Authentication authentication,
                                              @Valid @RequestBody MasterSettingDtos.PasswordChangeRequest request) {
        Claims claims = claims(authentication);
        service.changePassword(school(claims), staff(claims), request);
        return Map.of("message", "Password updated");
    }

    @PostMapping("/session-switch")
    public Map<String, Object> switchSession(Authentication authentication,
                                              @Valid @RequestBody MasterSettingDtos.SessionSwitchRequest request) {
        Claims claims = claims(authentication);
        int copied = service.switchSession(school(claims), staff(claims), request);
        return Map.of("message", "Session configuration copied", "rows", copied,
            "action", request.action(), "fromSession", request.fromSession(), "toSession", request.toSession());
    }

    @GetMapping("/session-switch/status")
    public Map<String, Boolean> sessionSwitchStatus(Authentication authentication,
                                                     @RequestParam String session) {
        Claims claims = claims(authentication);
        return service.sessionSwitchStatus(school(claims), session);
    }

    private static Claims claims(Authentication authentication) {
        if (authentication == null || !(authentication.getDetails() instanceof Claims claims))
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED, "Authenticated claims are missing");
        return claims;
    }

    private static String school(Claims claims) { return required(claims.get("schoolId", String.class)); }
    private static String staff(Claims claims) { return required(claims.get("staffUid", String.class)); }
    private static String required(String value) {
        if (value == null || value.isBlank())
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.UNAUTHORIZED, "Authenticated school context is missing");
        return value;
    }
}
