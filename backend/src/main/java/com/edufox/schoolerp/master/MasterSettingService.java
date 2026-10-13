package com.edufox.schoolerp.master;

import com.edufox.schoolerp.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MasterSettingService {
    private final MasterSettingRepository repository;
    private final AppProperties properties;

    public Map<String, Object> bootstrap(String schoolId, String staffUid) {
        requireContext(schoolId, staffUid);
        return repository.bootstrap(schoolId, staffUid);
    }

    public List<Map<String, Object>> logs(String schoolId, String from, String to, String page, String activity) {
        requireContext(schoolId, "logs");
        return repository.logs(schoolId, from, to, page, activity);
    }

    public List<Map<String, Object>> permissions(String schoolId, int designationId) {
        requireContext(schoolId, "permissions");
        return repository.permissionConfig(schoolId, designationId);
    }

    public List<Map<String, Object>> moduleMappings(String schoolId, String session) {
        requireContext(schoolId, "module-mappings");
        return repository.moduleMappings(schoolId, session == null || session.isBlank()
            ? repository.currentSession(schoolId) : session);
    }

    public void createClass(String schoolId, String staffUid, MasterSettingDtos.ClassSaveRequest request) {
        writeGuard();
        repository.insertClass(schoolId, staffUid, request.className(), request.classGroup());
    }

    public void createSections(String schoolId, String staffUid, MasterSettingDtos.ClassSaveRequest request) {
        writeGuard();
        repository.insertSection(schoolId, staffUid, request.classIds(), request.sectionName(), request.sectionType());
    }

    public void updateSection(String schoolId, int sectionId, MasterSettingDtos.SectionUpdateRequest request) {
        writeGuard();
        if (repository.updateSection(schoolId, sectionId, request.sectionName(), request.sectionType()) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found");
    }

    public void createDesignation(String schoolId, String staffUid, MasterSettingDtos.DesignationCreateRequest request) {
        writeGuard();
        repository.insertDesignation(schoolId, staffUid, request.designName(), request.designCode());
    }

    public void updateDesignation(String schoolId, String staffUid, int id,
                                  MasterSettingDtos.DesignationUpdateRequest request) {
        writeGuard();
        if (repository.updateDesignation(schoolId, id, request.designCode(), request.displayName(),
            request.displayOrder(), staffUid) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Designation not found");
    }

    public void createSession(String schoolId, String staffUid, MasterSettingDtos.SessionCreateRequest request) {
        writeGuard();
        try {
            repository.insertSession(schoolId, staffUid, request.session());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session must be in YYYY-YYYY format");
        }
    }

    public void setDefaultSession(String schoolId, MasterSettingDtos.SessionDefaultRequest request) {
        writeGuard();
        repository.setDefaultSession(schoolId, request.id());
    }

    public void saveTerm(String schoolId, String staffUid, MasterSettingDtos.TermSaveRequest request) {
        writeGuard();
        String session = repository.currentSession(schoolId);
        if (session.isBlank()) throw new ResponseStatusException(HttpStatus.CONFLICT, "No default session found");
        if (request.endDate().isBefore(request.startDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        repository.saveTerm(schoolId, staffUid, request, session);
    }

    public void saveModuleMapping(String schoolId, String staffUid, MasterSettingDtos.ModuleMappingRequest request) {
        writeGuard();
        repository.saveModuleMapping(schoolId, staffUid, request, repository.currentSession(schoolId));
    }

    public void savePermissions(String schoolId, String staffUid, MasterSettingDtos.PermissionSaveRequest request) {
        writeGuard();
        repository.savePermissions(schoolId, staffUid, request);
    }

    public void changePassword(String schoolId, String staffUid, MasterSettingDtos.PasswordChangeRequest request) {
        writeGuard();
        if (request.newPassword().length() < 8)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be at least 8 characters");
        if (repository.updatePassword(schoolId, staffUid, request.currentPassword(), request.newPassword()) == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
    }

    public void updateProfile(String schoolId, String staffUid, MasterSettingDtos.ProfileUpdateRequest request) {
        writeGuard();
        repository.updateProfile(schoolId, staffUid, request.fields());
    }

    private void writeGuard() {
        if (properties.getAuth().isDatabaseReadOnly() || !properties.getMasterSettings().isWriteEnabled()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Master Setting write operations are disabled for the read-only legacy database");
        }
    }

    private static void requireContext(String schoolId, String value) {
        if (schoolId == null || schoolId.isBlank() || value == null || value.isBlank())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated school context is missing");
    }
}
