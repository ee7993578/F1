package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StudentWriteRepository {
    private final JdbcTemplate jdbc;

    public StudentWriteDtos.WriteResponse register(
            String schoolId,
            String staffUid,
            StudentWriteDtos.RegistrationRequest request
    ) {
        String uid = UUID.randomUUID().toString();
        String registrationNo = text(request.formNo());
        if (registrationNo.isBlank()) registrationNo = nextFormNo(schoolId);

        Integer classId = findClassId(schoolId, request.className());
        String dob = dateText(request.dobYear(), request.dobMonth(), request.dobDay());

        jdbc.update("""
            INSERT INTO student_reg
              (uid, form_no, class_id, Registration_date, created_by, school_id,
               session, status, stutype, reg_fee, mop, dob, first_name, mid_name,
               last_name, gender, blood_group, child_cat, father_name, mother_name,
               r_address, mobile, phone, student_aadhar, religion, quota)
            VALUES
              (?, ?, ?, ?, ?, ?, ?, 'Registered', ?, ?, ?, TRY_CONVERT(date, ?, 23),
               ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            uid, registrationNo, classId, text(request.regDate()), staffUid, schoolId,
            text(request.session()), text(request.studType()), text(request.regFee()),
            text(request.mop()), dob, text(request.firstName()), text(request.middleName()),
            text(request.lastName()), text(request.gender()), text(request.blood()),
            text(request.category()), text(request.fName()), text(request.mName()),
            text(request.address()), text(request.fMobile()), text(request.mMobile()),
            text(request.aadhar()), text(request.religion()), text(request.quota())
        );


        jdbc.update("""
            INSERT INTO student_other_info
              (uid, school_id, Registration_NO, First_name, Mid_name, Last_name,
               DOB, Father_name, Mother_name, R_Address, Phone, Mobile, Gender,
               F_Qualification, M_Qualification, F_Ocp_Code, M_Ocp_Code,
               student_aadhar, father_aadhar, mother_aadhar, Religion, quota)
            VALUES
              (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            uid, schoolId, registrationNo, text(request.firstName()), text(request.middleName()),
            text(request.lastName()), dob, text(request.fName()), text(request.mName()),
            text(request.address()), text(request.fMobile()), text(request.mMobile()),
            text(request.gender()), text(request.fQual()), text(request.mQual()),
            text(request.fOcc()), text(request.mOcc()), text(request.aadhar()),
            text(request.fAadhar()), text(request.mAadhar()), text(request.religion()),
            text(request.quota())
        );

        return new StudentWriteDtos.WriteResponse(uid, registrationNo, "Student registration saved");
    }

    private String nextFormNo(String schoolId) {
        try {
            Object value = jdbc.queryForObject(
                "SELECT ISNULL(MAX(TRY_CONVERT(int, form_no)) + 1, 1001) FROM student_reg WHERE school_id=?",
                Object.class, schoolId
            );
            return value == null ? "1001" : String.valueOf(value);
        } catch (EmptyResultDataAccessException ex) {
            return "1001";
        }
    }

    private Integer findClassId(String schoolId, String className) {
        if (className == null || className.isBlank()) return null;
        try {
            return jdbc.queryForObject(
                "SELECT TOP 1 id FROM class_name WHERE school_id=? AND class_name=? ORDER BY id",
                Integer.class, schoolId, className
            );
        } catch (EmptyResultDataAccessException ex) {
            try { return Integer.valueOf(className); } catch (NumberFormatException ignored) { return null; }
        }
    }

    private static String dateText(String year, String month, String day) {
        if (year == null || year.isBlank() || month == null || month.isBlank() || day == null || day.isBlank()) return null;
        return String.format("%04d-%02d-%02d", Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
    }

    private static String text(String value) { return value == null ? "" : value.trim(); }
}