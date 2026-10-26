package com.edufox.schoolerp.student.work.online_registration;
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
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StudentOnlineRegistrationRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> detail(String schoolId, long id) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT * FROM onlineStudentRegistration WHERE school_id=? AND id=?
            """, schoolId, id);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    @Transactional
    public Map<String, Object> confirm(String schoolId, String staffUid,
                                       StudentOnlineRegistrationDtos.ConfirmRequest request) {
        String onlineRegNo = text(request.onlineRegNo());
        if (onlineRegNo.isBlank()) throw new IllegalArgumentException("Online registration number is required");
        List<Map<String, Object>> sourceRows = jdbc.queryForList("""
            SELECT TOP 1 * FROM onlineStudentRegistration
            WHERE school_id=? AND registrationNO=?
            ORDER BY id DESC
            """, schoolId, onlineRegNo);
        if (sourceRows.isEmpty()) throw new IllegalArgumentException("Online registration was not found");
        Map<String, Object> source = sourceRows.get(0);
        if (!"success".equalsIgnoreCase(text(source.get("paymentStatus")))) {
            throw new IllegalArgumentException("Only successful payments can be confirmed");
        }

        String registrationNo = text(request.registrationNo());
        String status = registrationNo.isBlank() ? "Admitted" : "Registered";
        String uid = nextNumber("student_reg", "uid", schoolId);
        String formNo = nextNumber("student_reg", "form_no", schoolId);
        String admissionNo = registrationNo.isBlank() ? onlineRegNo : registrationNo;
        String session = text(source.get("session"));
        String classId = text(source.get("admissionClass"));
        String paymentDate = text(source.get("paymentDate"));
        String admissionDate = text(request.admissionDate());
        String actor = text(staffUid);

        Integer existing = jdbc.queryForObject("""
            SELECT COUNT(*) FROM student_reg WHERE school_id=? AND OnlineRegNo=?
            """, Integer.class, schoolId, onlineRegNo);
        if (existing != null && existing > 0) {
            jdbc.update("UPDATE onlineStudentRegistration SET AdmissionStatus=?, AdmissionNo=? WHERE school_id=? AND registrationNO=?",
                "Confirm", admissionNo, schoolId, onlineRegNo);
            return detailByRegistration(schoolId, onlineRegNo);
        }

        jdbc.update("""
            INSERT INTO student_reg
              (uid,form_no,class_id,Registration_date,created_by,created_date,school_id,session,status,stutype,
               reg_fee,receipt_no,mop,transaction_no,amount,transaction_date,OnlineRegNo,Admission_date)
            VALUES (?,?,?,TRY_CONVERT(datetime,?,103),?,GETDATE(),?,?,?,1,?,?,?,?,?,?,?,TRY_CONVERT(datetime,?,103))
            """, uid, formNo, classId, paymentDate, actor, schoolId, session, status,
            text(source.get("paymentAmount")), text(source.get("reveiptNo")), text(source.get("paymentMode")),
            text(source.get("order_id")), text(source.get("paymentAmount")), paymentDate, onlineRegNo, admissionDate);

        jdbc.update("""
            INSERT INTO student_other_info
              (uid,school_id,Registration_NO,First_name,Mid_name,Last_name,DOB,Father_name,Mother_name,R_Address,
               Phone,Mobile,student_E_mail,father_E_mail,mother_E_mail,Gender,F_Qualification,M_Qualification,
               F_Ocp_Code,F_Org_Add,F_Org_Name,F_Designation,M_Ocp_Code,M_Org_Add,M_Org_Name,M_Designation,
               quota,previous_school_name,previous_school_aff,student_aadhar,Religion,Mother_Tongue,guardian_name,
               annual_income,birth_place,nationality,Previous_Class,permanent_address,stream,GuardianMobNo,
               GuardianEmail,EmergencyMobNo,FatherAge,MotherAge,GuardianAge,FatherIncome,MotherIncome,
               GuardianRelation,PEN_No,student_unique_id,SiblingId)
            SELECT ?,?, ?, COALESCE(firstName,''),COALESCE(midName,''),COALESCE(lastName,''),DOB,
                   COALESCE(fatherName,''),COALESCE(motherName,''),COALESCE(residentialAddr,''),
                   COALESCE(motherPhone,''),COALESCE(fatherMobile,''),COALESCE(studentEmail,''),COALESCE(fatherEmail,''),
                   COALESCE(motherEmail,''),COALESCE(gender,''),COALESCE(fatherQualification,''),COALESCE(motherQualification,''),
                   COALESCE(fatherOccupation,''),COALESCE(fatherOrganizationAddr,''),COALESCE(fatherOrganizationName,''),COALESCE(fatherDesignation,''),
                   COALESCE(motherOccupation,''),COALESCE(motherOrgAddr,''),COALESCE(motherOrgName,''),COALESCE(motherDesignation,''),
                   COALESCE((SELECT TOP 1 id FROM quota WHERE school_id=? AND quota=src.studentType),''),
                   COALESCE(prevSchoolName,''),COALESCE(prevSchoolAffNo,''),COALESCE(studentAadhar,''),
                   COALESCE(Religion,''),COALESCE(motherTongue,''),COALESCE(guardianName,''),COALESCE(fatherIncome,''),
                   COALESCE(birthPlace,''),COALESCE(nationality,''),COALESCE(prevSchoolName,''),COALESCE(residentialAddr,''),
                   COALESCE(stream,''),COALESCE(guardianMobNo,''),COALESCE(guardianEmail,''),COALESCE(EmergencyMobNo,''),
                   COALESCE(fatherAge,''),COALESCE(MotherAge,''),COALESCE(guardianAge,''),COALESCE(fatherIncome,''),
                   COALESCE(MotherIncome,''),COALESCE(guardianRelation,''),COALESCE(penNO,''),
                   UPPER(CONVERT(VARCHAR(8), CRYPT_GEN_RANDOM(4), 2)),
                   COALESCE((SELECT CAST(ISNULL(MAX(TRY_CONVERT(int,SiblingId)),0)+1 AS varchar(50)) FROM student_other_info WHERE school_id=?),'1')
            FROM onlineStudentRegistration src
            WHERE src.school_id=? AND src.registrationNO=?
            """, uid, schoolId, admissionNo, schoolId, schoolId, onlineRegNo);

        String secId = text(request.secId());
        if (!secId.isBlank() && !"0".equals(secId)) {
            String stuId = nextNumber("TB_StudentMaster", "stu_id", schoolId);
            jdbc.update("""
                INSERT INTO TB_StudentMaster
                  (uid,stu_id,Registration_NO,House_id,Class_id,sec_id,Admission_date,Registration_date,
                   status,stutype,quota,isnew,school_id,session,RollNo,FeeBookNo,Optional_sub)
                VALUES (?,?,?,0,?,?,?,TRY_CONVERT(datetime,?,103),'Registered',1,0,1,?,?,1,'', '')
                """, uid, stuId, admissionNo, classId, secId, admissionDate, paymentDate, schoolId, session);
        }
        jdbc.update("""
            UPDATE onlineStudentRegistration SET AdmissionStatus='Confirm', AdmissionNo=?
            WHERE school_id=? AND registrationNO=?
            """, admissionNo, schoolId, onlineRegNo);
        return detailByRegistration(schoolId, onlineRegNo);
    }

    @Transactional
    public Map<String, Object> updateStatus(String schoolId, StudentOnlineRegistrationDtos.StatusRequest request) {
        String value = text(request.status());
        if (value.isBlank()) throw new IllegalArgumentException("Status is required");
        int count = jdbc.update("""
            UPDATE onlineStudentRegistration SET AdmissionStatus=?
            WHERE school_id=? AND (registrationNO=? OR AdmissionNo=? OR stuAdmissionNo=?)
            """, value, schoolId, text(request.registrationNo()), text(request.registrationNo()), text(request.registrationNo()));
        if (count == 0) throw new IllegalArgumentException("Online registration was not found");
        return detailByRegistration(schoolId, text(request.registrationNo()));
    }

    @Transactional
    public Map<String, Object> updatePayment(String schoolId, StudentOnlineRegistrationDtos.PaymentRequest request) {
        int count = jdbc.update("""
            UPDATE onlineStudentRegistration
            SET order_id=COALESCE(NULLIF(?,''),order_id), reveiptNo=COALESCE(NULLIF(?,''),reveiptNo),
                paymentDate=COALESCE(NULLIF(?,''),paymentDate), paymentAmount=COALESCE(NULLIF(?,''),paymentAmount),
                paymentMode=COALESCE(NULLIF(?,''),paymentMode), paymentStatus='Success'
            WHERE school_id=? AND registrationNO=?
            """, text(request.orderId()), text(request.receiptNo()), text(request.paymentDate()),
            text(request.paymentAmount()), text(request.paymentMode()), schoolId, text(request.onlineRegNo()));
        if (count == 0) throw new IllegalArgumentException("Online registration was not found");
        return detailByRegistration(schoolId, text(request.onlineRegNo()));
    }

    private Map<String, Object> detailByRegistration(String schoolId, String registrationNo) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT TOP 1 * FROM onlineStudentRegistration WHERE school_id=? AND registrationNO=? ORDER BY id DESC", schoolId, registrationNo);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    private String nextNumber(String table, String column, String schoolId) {
        String safeTable = switch (table) { case "student_reg", "TB_StudentMaster" -> table; default -> throw new IllegalArgumentException("Unsupported table"); };
        String safeColumn = switch (column) { case "uid", "form_no", "stu_id" -> column; default -> throw new IllegalArgumentException("Unsupported column"); };
        Object value = jdbc.queryForObject("SELECT ISNULL(MAX(TRY_CONVERT(int," + safeColumn + ")),0)+1 FROM " + safeTable + " WHERE school_id=?", Object.class, schoolId);
        return value == null ? "1" : String.valueOf(value);
    }

    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
}
