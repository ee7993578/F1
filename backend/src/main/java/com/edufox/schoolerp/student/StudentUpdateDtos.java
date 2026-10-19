package com.edufox.schoolerp.student;

import java.util.List;

public final class StudentUpdateDtos {
    private StudentUpdateDtos() {}

    public record MoveRequest(
        String operation,
        String sourceSession,
        String targetSession,
        Integer sourceSectionId,
        Integer targetSectionId,
        List<String> studentUids,
        Boolean sameRegistrationNo,
        String sourceSchoolId,
        String targetSchoolId
    ) {}

    public record MoveResponse(int moved, String message) {}
}
