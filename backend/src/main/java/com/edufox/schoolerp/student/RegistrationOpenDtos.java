package com.edufox.schoolerp.student;

public final class RegistrationOpenDtos {
    private RegistrationOpenDtos() {}
    public record RegistrationRequest(Integer id, String session, String classId, String openDate, String closeDate,
        String minAgeYear, String minAgeMonth, String maxAgeYear, String maxAgeMonth,
        String feeAmount, String status, String studentType) {}
    public record RegistrationResponse(Integer id, String message) {}
}