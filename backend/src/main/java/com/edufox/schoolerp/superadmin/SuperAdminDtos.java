package com.edufox.schoolerp.superadmin;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class SuperAdminDtos {
    private SuperAdminDtos() {}

    public record LoginRequest(@NotBlank String identifier, @NotBlank String password) {}

    public record SchoolRequest(
        String schoolId,
        @NotBlank String schoolName,
        String websiteUrl,
        String affiliationNo,
        String schoolCode,
        String phone,
        @NotBlank String mobile,
        @NotBlank String email,
        @NotBlank String address,
        String pincode,
        Integer stateId,
        Integer cityId,
        String logoPath
    ) {}

    public record SelectionRequest(List<Integer> ids) {}

    public record PageMappingSaveRequest(
        @NotBlank String schoolId,
        Integer moduleId,
        List<PageMappingRow> pages
    ) {}

    public record PageMappingRow(
        Integer pageId,
        String displayName,
        Integer priority,
        boolean checked
    ) {}
}
