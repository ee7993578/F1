package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.sql.Date;

@Repository
@RequiredArgsConstructor
public class StudentWriteRepository {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final JdbcTemplate jdbc;

    /**
     * Direct Java replacement for the legacy insert_stu_reg_detail procedure.
     * Table names and legacy column names are intentionally preserved.
     */
    public StudentWriteDtos.WriteResponse register(
            String schoolId,
            String staffUid,
            StudentWriteDtos.RegistrationRequest request
    ) {
        String firstName = text(request.firstName());
        String session = text(request.session());
        if (firstName.isBlank()) throw new IllegalArgumentException("First name is required");
        if (session.isBlank()) throw new IllegalArgumentException("Session is required");

        String registrationNo = text(request.formNo());
        if (registrationNo.isBlank()) registrationNo = nextFormNo(schoolId);
        Integer classId = findClassId(schoolId, request.className());
        if (classId == null) throw new IllegalArgumentException("Admission class was not found");

        Integer duplicate = jdbc.queryForObject(
            "SELECT COUNT(*) FROM student_reg WHERE school_id=? AND session=? AND form_no=?",
            Integer.class, schoolId, session, registrationNo
        );
        if (duplicate != null && duplicate > 0) {
            throw new IllegalArgumentException("Registration number already exists for this session");
        }

        String uid = nextUid(schoolId);
        String studentCode = randomDigits(6);
        String parentCode = randomDigits(5);
        String studentPassword = studentCode;
        String parentPassword = firstName + parentCode;
        String studentId = studentCode + firstName;
        String parentId = firstName + parentCode;
        String quota = text(request.quota());
        String studentType = legacyStudentType(request.studType());
        Date dob = dateOf(request.dobYear(), request.dobMonth(), request.dobDay());
        Date registrationDate = dateOf(request.regDate());
        String receiptNo = nextReceiptNo(schoolId, session, request.mop());

        jdbc.update("""
            INSERT INTO student_reg
              (uid, form_no, class_id, Registration_date, created_by, school_id,
               session, status, stutype, reg_fee, receipt_no, mop)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'Admitted', ?, ?, ?, ?)
            """,
            uid, registrationNo, classId, registrationDate, text(staffUid), schoolId,
            session, studentType, text(request.regFee()), receiptNo, text(request.mop())
        );

        jdbc.update("""
            INSERT INTO student_other_info
              (uid, school_id, Registration_NO, First_name, Mid_name, Last_name,
               childcat, bloodgroup, DOB, Father_name, Mother_name, R_Address,
               Phone, Mobile, mother_E_mail, father_E_mail, District_id, State_id,
               Gender, Pin, Password, Parent_password, Student_id, Parent_id,
               F_Qualification, M_Qualification, F_Ocp_Code, M_Ocp_Code,
               studentimage, fatherimage, motherimage, quota, sibling_admno,
               sibling_class, sibling_section, previous_school_name,
               previous_school_aff, student_aadhar, remark, mother_aadhar,
               father_aadhar, Religion, Previous_Class, TransferCertificate,
               BirthCertificate, marksheet, Migration, Aadhar_Card,
               Cast_Certificate, annual_income, family_income, RTE, ncc, UDISE,
               PEN_No, APAAR_ID, account_holder, bank_name, branch_name,
               account_type, bank_acc, ifsc_code, groups, F_whats_no, M_whats_no)
             VALUES
               (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?)
             """,
             uid, schoolId, "", firstName, text(request.middleName()), text(request.lastName()),
             text(request.category()), text(request.blood()), dob,
             text(request.fName()), text(request.mName()), text(request.address()),
             text(request.mMobile()), text(request.fMobile()), text(request.mEmail()), text(request.fEmail()),
             resolveCity(request.city()), resolveState(request.state()), text(request.gender()), toInteger(request.pin()),
             studentPassword, parentPassword, studentId, parentId,
             text(request.fQual()), text(request.mQual()), text(request.fOcc()), text(request.mOcc()),
             text(request.studentImage()), text(request.fatherImage()), text(request.motherImage()), quota, text(request.sibAdm()), text(request.sibClass()), text(request.sibSection()),
             text(request.prevSchool()), text(request.affiliation()), text(request.aadhar()), text(request.remark()),
             text(request.mAadhar()), text(request.fAadhar()), text(request.religion()), text(request.previousClass()),
             text(request.transferCertificate()), text(request.birthCertificate()), text(request.marksheet()),
             text(request.migration()), text(request.aadharCard()), text(request.castCertificate()),
             text(request.fIncome()), text(request.mIncome()), isRte(quota), text(request.ncc()), text(request.udise()),
             text(request.pen()), text(request.apaar()), text(request.accHolder()), text(request.bank()),
             text(request.branch()), text(request.accType()), text(request.accNo()), text(request.ifsc()),
             text(request.group()), text(request.fWa()), text(request.mWa())
         );

         return new StudentWriteDtos.WriteResponse(uid, registrationNo, "Student registration saved");
     }

     private String nextUid(String schoolId) {
         Object value = jdbc.queryForObject(
             "SELECT ISNULL(MAX(TRY_CONVERT(int, uid)),0)+1 FROM student_reg WHERE school_id=?",
             Object.class, schoolId
         );
         return value == null ? "1" : String.valueOf(value);
     }

     private String nextFormNo(String schoolId) {
         try {
             Object value = jdbc.queryForObject(
                 "SELECT ISNULL(MAX(TRY_CONVERT(int, form_no))+1,1001) FROM student_reg WHERE school_id=?",
                 Object.class, schoolId
             );
             return value == null ? "1001" : String.valueOf(value);
         } catch (EmptyResultDataAccessException ex) {
             return "1001";
         }
     }

     private String nextReceiptNo(String schoolId, String session, String mop) {
         if (text(mop).isBlank() || "0".equals(text(mop))) return "0";
         Object value = jdbc.queryForObject(
             "SELECT ISNULL(MAX(TRY_CONVERT(int, receipt_no))+1,1001) FROM student_reg WHERE school_id=? AND session=?",
             Object.class, schoolId, session
         );
         return value == null ? "1001" : String.valueOf(value);
     }

     private Integer findClassId(String schoolId, String className) {
         if (className == null || className.isBlank()) return null;
         try {
             return jdbc.queryForObject(
                 "SELECT TOP 1 id FROM class_name WHERE school_id=? AND class_name=? ORDER BY id",
                 Integer.class, schoolId, className
             );
         } catch (EmptyResultDataAccessException ex) {
             try { return Integer.valueOf(className); }
             catch (NumberFormatException ignored) { return null; }
         }
     }

     private Integer resolveState(String state) {
         if (state == null || state.isBlank()) return null;
         try { return Integer.valueOf(state); }
         catch (NumberFormatException ignored) {
             return jdbc.query("SELECT TOP 1 state_id FROM state WHERE LOWER(state_name)=LOWER(?)",
                 rs -> rs.next() ? rs.getInt(1) : null, state.trim());
         }
     }

     private Integer resolveCity(String city) {
         if (city == null || city.isBlank()) return null;
         try { return Integer.valueOf(city); }
         catch (NumberFormatException ignored) {
             return jdbc.query("SELECT TOP 1 city_id FROM Districts WHERE LOWER(city_name)=LOWER(?)",
                 rs -> rs.next() ? rs.getInt(1) : null, city.trim());
         }
     }

     private static String legacyStudentType(String value) {
         String type = text(value);
         if (type.matches("\\d+")) return type;
         return "1";
     }

     private static boolean isRte(String quota) {
         return "rte".equalsIgnoreCase(quota) || "5".equals(quota);
     }

     private static Date dateOf(String year, String month, String day) {
         if (text(year).isBlank() || text(month).isBlank() || text(day).isBlank()) return null;
         try { return Date.valueOf(String.format("%04d-%02d-%02d", Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day))); }
         catch (RuntimeException ex) { throw new IllegalArgumentException("Date of birth is invalid"); }
     }

     private static Date dateOf(String value) {
         if (text(value).isBlank()) return null;
         try { return Date.valueOf(text(value)); }
         catch (IllegalArgumentException ex) { throw new IllegalArgumentException("Registration date is invalid"); }
     }

     private static Integer toInteger(String value) {
         try { return text(value).isBlank() ? null : Integer.valueOf(text(value)); }
         catch (NumberFormatException ex) { return null; }
     }

     private static String randomDigits(int length) {
         int lower = (int) Math.pow(10, length - 1);
         return String.valueOf(lower + RANDOM.nextInt(9 * lower));
     }

     private static String text(String value) { return value == null ? "" : value.trim(); }
 }

