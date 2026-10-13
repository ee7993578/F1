package com.edufox.schoolerp.student;

public final class StudentAdmissionDtos {
    private StudentAdmissionDtos() {}
    public record AdmissionRequest(
        String admissionNo, String feeBookNo, String className, Integer sectionId,
        String session, String admissionDate, String rollNo, String status
    ) {}
    public record AdmissionResponse(String uid, Integer studentId, String admissionNo, String message) {}
}