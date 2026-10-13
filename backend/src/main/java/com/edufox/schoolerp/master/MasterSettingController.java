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
                                          @RequestParam(required = false) String activity) {
        return service.logs(school(claims(authentication)), from, to, page, activity);
    }

    @GetMapping("/permissions/{designationId}")
    public List<Map<String, Object>> permissions(Authentication authentication,
                                                @PathVariable int designationId) {
        return service.permissions(school(claims(authentication)), designationId);
    }

    @GetMapping("/module-mappings")
    public List<Map<String, Object>> moduleMappings(Authentication authentication,
                                                    @RequestParam(required = false) String session) {
        return service.moduleMappings(school(claims(authentication)), session);
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
        service.updateSection(school(claims(authentication)), sectionId, request);
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
