package com.edufox.schoolerp.master;

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
    public record PermissionSaveRequest(@NotNull Integer designationId, @NotEmpty List<PermissionRow> permissions) {}
    public record PermissionRow(@NotNull Integer pageId, Integer moduleId, boolean view, boolean add,
                                boolean update, boolean delete, String classIds) {}
    public record PasswordChangeRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
    public record ProfileUpdateRequest(@NotNull Map<String, Object> fields) {}
}
