package com.edufox.schoolerp.staff;

import java.util.List;

public final class StaffDtos {
    private StaffDtos() {}

    public record MutationResponse(String id, String message) {}
    public record ClassTeacherAssignment(String sectionId, String facultyId, String coFacultyId) {}
    public record ClassTeacherRequest(String session, List<ClassTeacherAssignment> assignments) {}
}
