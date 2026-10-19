package com.edufox.schoolerp.student;

public final class AlumniDtos {
    private AlumniDtos() {}
    public record AlumniRequest(String fullName,String studentId,String batch,String dob,String qualification,
        String currentStatus,String emailAddress,String phoneNumber,String gender,String address,String alumniPhoto,
        String alumniPhotoBase64,String alumniPhotoName) {}
    public record AlumniResponse(Integer id,String message) {}
}
