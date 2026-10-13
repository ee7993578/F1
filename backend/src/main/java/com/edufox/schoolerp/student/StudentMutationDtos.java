package com.edufox.schoolerp.student;

public final class StudentMutationDtos {
    private StudentMutationDtos() {}

    public record UpdateRequest(
        String registrationNo, String firstName, String middleName, String lastName,
        String dob, String gender, String bloodGroup, String category, String quota,
        String fatherName, String motherName, String address, String phone, String mobile,
        String studentAadhar, String religion, String status, String className,
        String session, Integer sectionId, String remark
    ) {}

    public record MutationResponse(String uid, String message) {}
}