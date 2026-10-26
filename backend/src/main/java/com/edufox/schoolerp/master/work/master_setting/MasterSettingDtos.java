package com.edufox.schoolerp.master.work.master_setting;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class MasterSettingDtos {
    private MasterSettingDtos() {}

    public record ClassSaveRequest(@NotBlank String className, String classGroup, Integer classId,
                                   String sectionName, List<Integer> classIds, String sectionType) {}
    public record SectionUpdateRequest(@NotBlank String sectionName, String sectionType) {}
    public record DesignationCreateRequest(@NotBlank String designName, String designCode) {}
    public record DesignationUpdateRequest(@NotBlank String designCode, @NotBlank String displayName,
                                           @NotNull Integer displayOrder) {}
    public record SessionCreateRequest(@NotBlank String session) {}
    public record SessionDefaultRequest(@NotNull Integer id) {}
    public record TermSaveRequest(@NotNull Integer classId, @NotNull Integer term,
                                  @NotNull LocalDate startDate, @NotNull LocalDate endDate, Integer id) {}
    public record ModuleMappingRequest(@NotBlank String moduleId, @NotEmpty List<String> classIds) {}
    public record PermissionSaveRequest(@NotNull Integer designationId, @NotNull List<PermissionRow> permissions) {}
    public record PermissionRow(@NotNull Integer pageId, Integer moduleId, boolean view, boolean add,
                                boolean update, boolean delete, String classIds) {}
    public record StaffPermissionSaveRequest(@NotNull Integer facultyId, @NotNull Integer moduleId,
                                             @NotNull List<Integer> pageIds, String classIds) {}
    public record MobilePermissionSaveRequest(@NotNull Integer designationId, @NotNull List<Integer> pageIds,
                                              @NotBlank String menuType) {}
    public record StaffRoleSaveRequest(@NotNull Integer facultyId, @NotNull List<Integer> designationIds) {}
    public record ModulePageMappingRequest(@NotBlank String schoolId, @NotNull Integer moduleId,
                                           @NotEmpty List<ModulePageRow> pages) {}
    public record ModulePageRow(@NotNull Integer pageId, String displayName, Integer priority, boolean checked) {}
    public record PasswordChangeRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
    public record ProfileUpdateRequest(@NotNull Map<String, Object> fields) {}
    public record SessionSwitchRequest(@NotBlank String fromSession, @NotBlank String toSession,
                                       @NotBlank String action) {}
}
