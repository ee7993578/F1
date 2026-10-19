package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StudentMutationRepository {
    private final JdbcTemplate jdbc;

    @Transactional
    public StudentMutationDtos.MutationResponse update(String schoolId, String uid, StudentMutationDtos.UpdateRequest request) {
        if (request.registrationNo() != null && !request.registrationNo().isBlank()) {
            Integer duplicate = jdbc.queryForObject("SELECT COUNT(*) FROM TB_StudentMaster WHERE school_id=? AND registration_no=? AND uid<>?", Integer.class, schoolId, request.registrationNo().trim(), uid);
            if (duplicate != null && duplicate > 0) throw new IllegalArgumentException("Admission number already exists");
        }
        List<String> otherSet = new ArrayList<>();
        List<Object> otherArgs = new ArrayList<>();
        add(otherSet, otherArgs, "Registration_NO", request.registrationNo());
        add(otherSet, otherArgs, "First_name", request.firstName());
        add(otherSet, otherArgs, "Mid_name", request.middleName());
        add(otherSet, otherArgs, "Last_name", request.lastName());
        add(otherSet, otherArgs, "DOB", request.dob());
        add(otherSet, otherArgs, "Gender", request.gender());
        add(otherSet, otherArgs, "bloodgroup", request.bloodGroup());
        add(otherSet, otherArgs, "childcat", request.category());
        add(otherSet, otherArgs, "quota", request.quota());
        add(otherSet, otherArgs, "Father_name", request.fatherName());
        add(otherSet, otherArgs, "Mother_name", request.motherName());
        add(otherSet, otherArgs, "R_Address", request.address());
        add(otherSet, otherArgs, "Phone", request.phone());
        add(otherSet, otherArgs, "Mobile", request.mobile());
        add(otherSet, otherArgs, "student_aadhar", request.studentAadhar());
        add(otherSet, otherArgs, "Religion", request.religion());
        add(otherSet, otherArgs, "student_E_mail", request.email());
        add(otherSet, otherArgs, "F_Ocp_Code", request.fatherOccupation());
        add(otherSet, otherArgs, "M_Ocp_Code", request.motherOccupation());
        add(otherSet, otherArgs, "F_whats_no", request.fatherWhatsapp());
        add(otherSet, otherArgs, "M_whats_no", request.motherWhatsapp());
        add(otherSet, otherArgs, "District_id", request.cityId());
        add(otherSet, otherArgs, "State_id", request.stateId());
        add(otherSet, otherArgs, "PEN_No", request.penNo());
        add(otherSet, otherArgs, "APAAR_ID", request.apaarId());
        add(otherSet, otherArgs, "UDISE", request.udise());
        add(otherSet, otherArgs, "att_date", request.attendanceDate());
        add(otherSet, otherArgs, "TransferCertificate", request.transferCertificate());
        add(otherSet, otherArgs, "BirthCertificate", request.birthCertificate());
        add(otherSet, otherArgs, "Marksheet", request.marksheet());
        add(otherSet, otherArgs, "Migration", request.migration());
        add(otherSet, otherArgs, "Aadhar_Card", request.aadharCard());
        add(otherSet, otherArgs, "Cast_Certificate", request.castCertificate());
        add(otherSet, otherArgs, "previous_school_name", request.previousSchool());
        add(otherSet, otherArgs, "previous_school_aff", request.previousSchoolAffiliation());
        add(otherSet, otherArgs, "Previous_Class", request.previousClass());
        add(otherSet, otherArgs, "F_Qualification", request.fatherQualification());
        add(otherSet, otherArgs, "M_Qualification", request.motherQualification());
        add(otherSet, otherArgs, "father_E_mail", request.fatherEmail());
        add(otherSet, otherArgs, "mother_E_mail", request.motherEmail());
        add(otherSet, otherArgs, "F_Org_Add", request.fatherOrgAddress());
        add(otherSet, otherArgs, "M_Org_Add", request.motherOrgAddress());
        add(otherSet, otherArgs, "annual_income", request.fatherIncome());
        add(otherSet, otherArgs, "family_income", request.motherIncome());
        add(otherSet, otherArgs, "father_aadhar", request.fatherAadhar());
        add(otherSet, otherArgs, "mother_aadhar", request.motherAadhar());
        add(otherSet, otherArgs, "account_holder", request.accountHolder());
        add(otherSet, otherArgs, "bank_name", request.bankName());
        add(otherSet, otherArgs, "branch_name", request.branchName());
        add(otherSet, otherArgs, "account_type", request.accountType());
        add(otherSet, otherArgs, "bank_acc", request.accountNo());
        add(otherSet, otherArgs, "ifsc_code", request.ifscCode());
        add(otherSet, otherArgs, "groups", request.studentGroup());
        add(otherSet, otherArgs, "ncc", request.ncc());
        add(otherSet, otherArgs, "stream", request.stream());
        add(otherSet, otherArgs, "SiblingId", request.siblingId());
        add(otherSet, otherArgs, "sibling_admno", request.siblingAdmNo());
        if (request.quota() != null && !request.quota().isBlank()) {
            otherSet.add("RTE=?");
            otherArgs.add(isRteQuota(schoolId, request.quota()) ? 1 : 0);
        }
        if (!otherSet.isEmpty()) {
            otherArgs.add(uid); otherArgs.add(schoolId);
            jdbc.update("UPDATE student_other_info SET " + String.join(", ", otherSet) + " WHERE uid=? AND school_id=?", otherArgs.toArray());
        }

        List<Object> masterArgs = new ArrayList<>();
        List<String> masterSet = new ArrayList<>();
        if (request.registrationNo() != null && !request.registrationNo().isBlank()) {
            masterSet.add("old_registration_no=CASE WHEN ISNULL(registration_no,'')<>? THEN registration_no ELSE old_registration_no END");
            masterArgs.add(request.registrationNo().trim());
            masterSet.add("registration_no=?"); masterArgs.add(request.registrationNo().trim());
        }
        add(masterSet, masterArgs, "status", request.status());
        add(masterSet, masterArgs, "session", request.session());
        add(masterSet, masterArgs, "Admission_date", request.admissionDate());
        add(masterSet, masterArgs, "Registration_date", request.registrationDate());
        add(masterSet, masterArgs, "RollNo", request.rollNo());
        add(masterSet, masterArgs, "FeeBookNo", request.feeBookNo());
        add(masterSet, masterArgs, "Optional_sub", request.optionalSubject());
        add(masterSet, masterArgs, "Stream_id", request.stream());
        if (request.status() != null) {
            masterSet.add("Withdrawn_date=CASE WHEN ? <> 'Registered' THEN COALESCE(?, CONVERT(varchar(30),GETDATE(),106)) ELSE NULL END");
            masterArgs.add(request.status().trim());
            masterArgs.add(request.withdrawnDate());
            masterSet.add("WithdrawnSession=?"); masterArgs.add(request.withdrawnSession());
            masterSet.add("Withdrawn_reason=?"); masterArgs.add(request.remark());
        }
        if (request.sectionId() != null) { masterSet.add("sec_id=?"); masterArgs.add(request.sectionId()); }
        if (request.houseId() != null && !request.houseId().isBlank()) {
            Integer houseId = findHouseId(schoolId, request.houseId());
            if (houseId != null) { masterSet.add("House_id=?"); masterArgs.add(houseId); }
        }
        if (request.className() != null && !request.className().isBlank()) {
            Integer classId = findClassId(schoolId, request.className());
            if (classId != null) { masterSet.add("class_id=?"); masterArgs.add(classId); }
        }
        if (!masterSet.isEmpty()) {
            masterArgs.add(uid); masterArgs.add(schoolId);
            jdbc.update("UPDATE TB_StudentMaster SET " + String.join(", ", masterSet) + " WHERE uid=? AND school_id=?", masterArgs.toArray());
        }

        // student_reg contains only registration workflow columns. Keep this
        // update limited to real columns; student identity fields belong to
        // student_other_info/TB_StudentMaster above.
        List<String> regSet = new ArrayList<>();
        List<Object> regArgs = new ArrayList<>();
        add(regSet, regArgs, "session", request.session());
        add(regSet, regArgs, "status", request.status());
        add(regSet, regArgs, "Admission_date", request.admissionDate());
        if (request.className() != null && !request.className().isBlank()) {
            Integer classId = findClassId(schoolId, request.className());
            if (classId != null) { regSet.add("class_id=?"); regArgs.add(classId); }
        }
        if (!regSet.isEmpty()) {
            regArgs.add(uid); regArgs.add(schoolId);
            jdbc.update("UPDATE student_reg SET " + String.join(", ", regSet) + " WHERE uid=? AND school_id=?", regArgs.toArray());
        }

        if (request.registrationNo() != null && !request.registrationNo().isBlank()) {
            jdbc.update("UPDATE student_other_info SET old_registration_no=CASE WHEN ISNULL(registration_no,'')<>? THEN registration_no ELSE old_registration_no END, Registration_NO=?, Student_id=?, Password=? WHERE uid=? AND school_id=?",
                request.registrationNo().trim(), request.registrationNo().trim(), request.registrationNo().trim(), request.registrationNo().trim(), uid, schoolId);
        }

        // The old student_withdrawn page calls InsertTcNo.  Keep the same
        // transfer-certificate row behaviour here with parameterized SQL.
        if (request.tcNo() != null || request.status() != null) {
            syncTransferCertificate(schoolId, uid, request);
        }

        return new StudentMutationDtos.MutationResponse(uid, "Student record updated");
    }

    @Transactional
    public StudentMutationDtos.MutationResponse softDelete(String schoolId, String uid) {
        jdbc.update("UPDATE student_reg SET status='Deleted' WHERE uid=? AND school_id=?", uid, schoolId);
        jdbc.update("UPDATE TB_StudentMaster SET status='Deleted' WHERE uid=? AND school_id=?", uid, schoolId);
        return new StudentMutationDtos.MutationResponse(uid, "Student record deleted");
    }

    @Transactional
    public StudentMutationDtos.MutationResponse updateWithdrawnReason(String schoolId, StudentMutationDtos.WithdrawReasonRequest request) {
        if (request == null || request.registrationNo() == null || request.registrationNo().isBlank()
                || request.session() == null || request.session().isBlank()) {
            throw new IllegalArgumentException("Registration number and session are required");
        }
        int updated = jdbc.update("""
            UPDATE TB_StudentMaster
               SET Withdrawn_reason=?
             WHERE school_id=? AND session=? AND status='Withdrawn' AND Registration_NO=?
            """, request.reason() == null ? "" : request.reason().trim(), schoolId,
            request.session().trim(), request.registrationNo().trim());
        if (updated == 0) throw new IllegalArgumentException("Withdrawn student was not found");
        return new StudentMutationDtos.MutationResponse(request.registrationNo().trim(), "Remark updated successfully");
    }

    private Integer findClassId(String schoolId, String className) {
        try {
            return jdbc.queryForObject("SELECT TOP 1 id FROM class_name WHERE school_id=? AND class_name=? ORDER BY id", Integer.class, schoolId, className);
        } catch (Exception ex) {
            try { return Integer.valueOf(className); } catch (NumberFormatException ignored) { return null; }
        }
    }

    private Integer findHouseId(String schoolId, String value) {
        try { return Integer.valueOf(value.trim()); }
        catch (NumberFormatException ignored) {
            try { return jdbc.queryForObject("SELECT TOP 1 House_id FROM HouseMaster WHERE school_id=? AND H_Name=? AND status=1", Integer.class, schoolId, value.trim()); }
            catch (Exception ignoredAgain) { return null; }
        }
    }

    private boolean isRteQuota(String schoolId, String value) {
        try {
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM quota WHERE school_id=? AND UPPER(quota)='RTE' AND (CAST(id AS varchar(50))=? OR UPPER(quota)=UPPER(?))", Integer.class, schoolId, value.trim(), value.trim());
            return count != null && count > 0;
        } catch (Exception ignored) {
            return "RTE".equalsIgnoreCase(value.trim());
        }
    }

    private void syncTransferCertificate(String schoolId, String uid, StudentMutationDtos.UpdateRequest request) {
        String session = request.session();
        if (session == null || session.isBlank()) return;
        List<Map<String, Object>> students = jdbc.queryForList("""
            SELECT TOP 1 sm.stu_id AS stuId, sm.registration_no AS registrationNo,
                   sm.Admission_date AS admissionDate, sm.sec_id AS sectionId,
                   so.dob AS dob, so.childcat AS category,
                   LTRIM(RTRIM(CONCAT(COALESCE(so.first_name,''),' ',COALESCE(so.mid_name,''),' ',COALESCE(so.last_name,'')))) AS studentName
            FROM TB_StudentMaster sm
            LEFT JOIN student_other_info so ON so.uid=sm.uid AND so.school_id=sm.school_id
            WHERE sm.school_id=? AND sm.uid=? AND sm.session=?
            ORDER BY sm.stu_id DESC
            """, schoolId, uid, session);
        if (students.isEmpty()) return;
        Map<String, Object> student = students.get(0);
        String tcNo = request.tcNo() == null ? "" : request.tcNo().trim();
        String effectiveDate = request.withdrawnDate() == null ? "" : request.withdrawnDate().trim();
        String status = request.status() == null ? "" : request.status().trim();
        Integer existing = jdbc.queryForObject("SELECT COUNT(*) FROM transfer_certificate WHERE school_id=? AND stu_id=? AND session=?", Integer.class,
            schoolId, String.valueOf(student.get("stuId")), session);
        if (!tcNo.isBlank()) {
            if (existing != null && existing > 0) {
                jdbc.update("UPDATE transfer_certificate SET tc_no=?, issue_date=?, modified_date=GETDATE(), modified_by=? WHERE school_id=? AND stu_id=? AND session=?",
                    tcNo, effectiveDate, uid, schoolId, String.valueOf(student.get("stuId")), session);
            } else {
                jdbc.update("""
                    INSERT INTO transfer_certificate
                      (tc_no,stu_id,stu_category,dob,issue_date,admission_date,promote_status,promoted_class,
                       last_class,last_date_of_att,nationality,failed_status,last_exam_result,qualified_status,
                       paid_fee_status,fee_concession,ncc_cadet_status,struck_off_date,no_of_meeting,general_conduct,
                       reason_for_leaving,remark,session,extra_activity,enrollment_no,school_id,created_date,created_by,
                       modified_date,modified_by,student_name,registration_no)
VALUES (?,?,?,?,?,?,'0','0',?,GETDATE(),'Indian','0','','0','0','0','0','','','',?,?,?,'','',?,GETDATE(),?,GETDATE(),?,?,?)
                    """,
                    tcNo, String.valueOf(student.get("stuId")), String.valueOf(student.getOrDefault("category", "")),
                    String.valueOf(student.getOrDefault("dob", "")), effectiveDate,
                    String.valueOf(student.getOrDefault("admissionDate", "")), String.valueOf(student.getOrDefault("sectionId", "")),
                    "", request.remark() == null ? "" : request.remark(), session, schoolId, uid, uid,
                    String.valueOf(student.getOrDefault("studentName", "")), String.valueOf(student.getOrDefault("registrationNo", "")));
            }
        } else if (existing != null && existing > 0) {
            // InsertTcNo(action=insert, tcno='') archives and removes an old
            // certificate when the row is restored to Registered.
            jdbc.update("""
                INSERT INTO transfer_certificate_log
                  (tc_no,stu_id,stu_category,dob,issue_date,admission_date,promote_status,promoted_class,last_class,
                   last_date_of_att,nationality,failed_status,last_exam_result,qualified_status,paid_fee_status,
                   fee_concession,ncc_cadet_status,struck_off_date,no_of_meeting,general_conduct,reason_for_leaving,
                   remark,session,extra_activity,enrollment_no,school_id,created_date,created_by,modified_date,
                   modified_by,total_att,tc_fee,working_days,application_date,subjects_studied,promote_class_word,
                   last_class_word,admission_class,subjects,school_no,book_no,affiliation_no,renew,registration_no,
                   file_path,removal_date,NCC_prov,ncc_cadet,file_path_datetime,ActionDate,ActionBy,ActionType,student_name)
                SELECT tc_no,stu_id,stu_category,dob,issue_date,admission_date,promote_status,promoted_class,last_class,
                       last_date_of_att,nationality,failed_status,last_exam_result,qualified_status,paid_fee_status,
                       fee_concession,ncc_cadet_status,struck_off_date,no_of_meeting,general_conduct,reason_for_leaving,
                       remark,session,extra_activity,enrollment_no,school_id,created_date,created_by,modified_date,
                       modified_by,total_att,tc_fee,working_days,application_date,subjects_studied,promote_class_word,
                       last_class_word,admission_class,subjects,school_no,book_no,affiliation_no,renew,registration_no,
                       file_path,removal_date,NCC_prov,ncc_cadet,file_path_datetime,GETDATE(),?, 'Delete', student_name
                FROM transfer_certificate WHERE school_id=? AND stu_id=? AND session=?
                """, uid, schoolId, String.valueOf(student.get("stuId")), session);
            jdbc.update("DELETE FROM transfer_certificate WHERE school_id=? AND stu_id=? AND session=?",
                schoolId, String.valueOf(student.get("stuId")), session);
        }
    }

    private static void add(List<String> set, List<Object> args, String column, String value) {
        if (value != null) { set.add(column + "=?"); args.add(value.trim()); }
    }
}
