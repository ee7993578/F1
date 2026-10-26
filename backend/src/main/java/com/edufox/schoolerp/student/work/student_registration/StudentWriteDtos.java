package com.edufox.schoolerp.student.work.student_registration;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_work.*;

public final class StudentWriteDtos {
    private StudentWriteDtos() {}

    public record RegistrationRequest(
        String formNo, String className, String session, String studType,
        String mop, String regFee, String regDate,
        String firstName, String middleName, String lastName,
        String dobDay, String dobMonth, String dobYear, String gender,
        String blood, String religion, String category, String quota, String ncc,
        String address, String state, String city, String pin, String aadhar, String group,
        String fName, String fQual, String fOcc, String fMobile, String fEmail,
        String fAadhar, String fIncome, String fWa,
        String mName, String mQual, String mOcc, String mMobile, String mEmail,
        String mAadhar, String mIncome, String mWa,
        String accHolder, String bank, String branch, String accType, String accNo, String ifsc,
        String sibAdm, String sibClass, String sibSection,
        String prevSchool, String affiliation, String udise, String pen, String apaar,
        String remark,
        String previousClass, String transferCertificate, String birthCertificate,
        String marksheet, String migration, String aadharCard, String castCertificate,
        String studentImage, String fatherImage, String motherImage
    ) {}

    public record WriteResponse(String uid, String registrationNo, String message) {}
}
