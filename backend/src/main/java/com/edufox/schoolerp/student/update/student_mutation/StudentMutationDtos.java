package com.edufox.schoolerp.student.update.student_mutation;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

public final class StudentMutationDtos {
    private StudentMutationDtos() {}

    public record UpdateRequest(
        String registrationNo, String firstName, String middleName, String lastName,
        String dob, String gender, String bloodGroup, String category, String quota,
        String fatherName, String motherName, String address, String phone, String mobile,
        String studentAadhar, String religion, String status, String className,
        String session, Integer sectionId, String remark, String rollNo, String feeBookNo,
        String houseId, String penNo, String apaarId, String udise, String email,
        String fatherOccupation, String motherOccupation, String fatherWhatsapp, String motherWhatsapp,
        String stateId, String cityId, String admissionDate, String registrationDate, String attendanceDate,
        String transferCertificate, String birthCertificate, String marksheet, String migration,
        String aadharCard, String castCertificate,
        String previousSchool, String previousSchoolAffiliation, String previousClass,
        String fatherQualification, String motherQualification, String fatherEmail, String motherEmail,
        String fatherOrgAddress, String motherOrgAddress, String fatherIncome, String motherIncome,
        String fatherAadhar, String motherAadhar, String accountHolder, String bankName, String branchName,
        String accountType, String accountNo, String ifscCode, String studentGroup,
        String ncc, String stream, String optionalSubject, String siblingId, String siblingAdmNo,
        String withdrawnDate, String withdrawnSession, String tcNo
    ) {}

    public record MutationResponse(String uid, String message) {}

    public record WithdrawReasonRequest(String registrationNo, String session, String reason) {}
}
