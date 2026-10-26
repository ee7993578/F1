package com.edufox.schoolerp.student.update.student_update;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StudentUpdateRepository {
    private final JdbcTemplate jdbc;

    @Transactional
    public StudentUpdateDtos.MoveResponse move(String schoolId, StudentUpdateDtos.MoveRequest request) {
        if (request == null || request.studentUids() == null || request.studentUids().isEmpty()) {
            throw new IllegalArgumentException("Select at least one student");
        }
        if (request.sourceSession() == null || request.targetSession() == null
            || request.sourceSectionId() == null || request.targetSectionId() == null) {
            throw new IllegalArgumentException("Source and destination session/class are required");
        }
        String operation = request.operation() == null ? "SECTION" : request.operation().trim().toUpperCase();
        String sourceSchool = blank(request.sourceSchoolId()) ? schoolId : request.sourceSchoolId().trim();
        String targetSchool = blank(request.targetSchoolId()) ? schoolId : request.targetSchoolId().trim();
        int moved = 0;
        for (String student : request.studentUids()) {
            if (blank(student)) continue;
            moved += switch (operation) {
                case "PROMOTE" -> promote(sourceSchool, request.sourceSession(), request.targetSession(),
                    request.sourceSectionId(), request.targetSectionId(), student);
                case "DEMOTE" -> demote(sourceSchool, request.targetSession(), student);
                case "SECTION" -> changeSection(sourceSchool, request.targetSession(), request.sourceSectionId(),
                    request.targetSectionId(), student);
                case "TRANSFER" -> transfer(sourceSchool, targetSchool, request.sourceSession(), request.targetSession(),
                    request.sourceSectionId(), request.targetSectionId(), student, Boolean.TRUE.equals(request.sameRegistrationNo()));
                default -> throw new IllegalArgumentException("Unsupported student movement operation");
            };
        }
        return new StudentUpdateDtos.MoveResponse(moved, moved + " student record(s) updated");
    }

    public List<Map<String, Object>> schools(String schoolId) {
        return jdbc.queryForList("""
            SELECT sm.school_id AS schoolId, sm.schoolname AS schoolName
            FROM schoolmaster sm
            WHERE sm.campusid=(SELECT campusid FROM schoolmaster WHERE school_id=?)
            ORDER BY sm.schoolname
            """, schoolId);
    }

    public Map<String, Object> options(String currentSchool, String requestedSchool) {
        String school = blank(requestedSchool) ? currentSchool : requestedSchool.trim();
        Integer allowed = jdbc.queryForObject("""
            SELECT COUNT(*) FROM schoolmaster target
            WHERE target.school_id=? AND target.campusid=(SELECT campusid FROM schoolmaster WHERE school_id=?)
            """, Integer.class, school, currentSchool);
        if (allowed == null || allowed == 0) throw new IllegalArgumentException("School is not available for transfer");
        return Map.of(
            "schoolId", school,
            "sessions", jdbc.queryForList("SELECT id,session,status FROM TB_Session WHERE school_id=? ORDER BY id DESC", school),
            "sections", jdbc.queryForList("""
                SELECT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,COALESCE(n.class_name,'') AS className
                FROM TB_Class c LEFT JOIN class_name n ON n.school_id=c.school_id AND n.id=c.class_id
                WHERE c.school_id=? ORDER BY c.class_id,c.section,c.sec_id
                """, school)
        );
    }

    private int changeSection(String schoolId, String session, int sourceSection, int targetSection, String student) {
        Map<String, Object> target = section(schoolId, targetSection);
        return jdbc.update("""
            UPDATE TB_StudentMaster
            SET class_id=?, sec_id=?, ModifiedDate=GETDATE()
            WHERE school_id=? AND session=? AND sec_id=?
              AND (CAST(stu_id AS varchar(100))=? OR uid=?)
            """, target.get("classId"), targetSection, schoolId, session, sourceSection, student, student);
    }

    private int promote(String schoolId, String sourceSession, String targetSession,
                        int sourceSection, int targetSection, String student) {
        Map<String, Object> source = student(schoolId, sourceSession, sourceSection, student);
        if (source == null) return 0;
        Map<String, Object> target = section(schoolId, targetSection);
        String registration = text(source.get("registrationNo"));
        Integer exists = jdbc.queryForObject("SELECT COUNT(*) FROM TB_StudentMaster WHERE school_id=? AND session=? AND registration_no=?",
            Integer.class, schoolId, targetSession, registration);
        if (exists != null && exists > 0) return 0;
        Integer nextStudentId = jdbc.queryForObject("SELECT ISNULL(MAX(stu_id),0)+1 FROM TB_StudentMaster WHERE school_id=?", Integer.class, schoolId);
        String nextFeeBook = jdbc.queryForObject("SELECT CAST(ISNULL(MAX(TRY_CONVERT(int,FeeBookNo)),0)+1 AS varchar(50)) FROM TB_StudentMaster WHERE school_id=? AND session=?", String.class, schoolId, targetSession);
        int inserted = jdbc.update("""
            INSERT INTO TB_StudentMaster
              (uid,stu_id,Registration_NO,old_Registration_NO,House_id,Class_id,sec_id,Admission_date,Registration_date,
               Withdrawn_date,Withdrawn_reason,status,stutype,quota,isnew,activity_id,readmission,school_id,session,
               FeeBookNo,Stream_id,Optional_sub)
            SELECT uid,?,Registration_NO,old_Registration_NO,House_id,?, ?,Admission_date,Registration_date,
                   NULL,NULL,'Registered',stutype,quota,0,activity_id,readmission,school_id,?, ?,Stream_id,Optional_sub
            FROM TB_StudentMaster
            WHERE school_id=? AND session=? AND (CAST(stu_id AS varchar(100))=? OR uid=?)
            """, nextStudentId, target.get("classId"), targetSection, targetSession, nextFeeBook,
            schoolId, sourceSession, student, student);
        if (inserted > 0) createBasicLedger(schoolId, targetSession, targetSection, nextStudentId);
        return inserted;
    }

    private int demote(String schoolId, String session, String student) {
        Map<String, Object> row = jdbc.queryForMap("""
            SELECT TOP 1 stu_id FROM TB_StudentMaster
            WHERE school_id=? AND session=? AND (CAST(stu_id AS varchar(100))=? OR uid=?)
            """, schoolId, session, student, student);
        Object stuId = row.get("stu_id");
        Integer paid = jdbc.queryForObject("SELECT COUNT(*) FROM fee_student_ledger WHERE school_id=? AND session=? AND stu_id=? AND COALESCE(receive_amount,0)>0", Integer.class, schoolId, session, stuId);
        if (paid != null && paid > 0) return 0;
        int changed = jdbc.update("DELETE FROM TB_StudentMaster WHERE school_id=? AND session=? AND stu_id=?", schoolId, session, stuId);
        jdbc.update("DELETE FROM fee_student_ledger WHERE school_id=? AND session=? AND stu_id=?", schoolId, session, stuId);
        jdbc.update("DELETE FROM fee_concession WHERE school_id=? AND session=? AND stu_id=?", schoolId, session, stuId);
        jdbc.update("DELETE FROM fee_Assign_Transport_Mapping WHERE school_id=? AND stu_id=?", schoolId, stuId);
        jdbc.update("DELETE FROM fee_Assign_Transport WHERE school_id=? AND session=? AND stu_id=?", schoolId, session, stuId);
        return changed;
    }

    private int transfer(String sourceSchool, String targetSchool, String sourceSession, String targetSession,
                         int sourceSection, int targetSection, String student, boolean sameRegistration) {
        Map<String, Object> source = student(sourceSchool, sourceSession, sourceSection, student);
        if (source == null) return 0;
        Map<String, Object> target = section(targetSchool, targetSection);
        String sourceUid = text(source.get("uid"));
        String sourceRegistration = text(source.get("registrationNo"));
        String targetUid = nextString("uid", "student_reg", targetSchool);
        String targetStuId = nextString("stu_id", "TB_StudentMaster", targetSchool);
        String formNo = nextString("form_no", "student_reg", targetSchool);
        String registration = sameRegistration ? sourceRegistration : "";
        boolean targetHasSession = count("SELECT COUNT(*) FROM TB_StudentMaster WHERE school_id=? AND session=?", targetSchool, targetSession) > 0;
        String status = (sameRegistration && targetHasSession) ? "Registered" : "Pending";

        jdbc.update("""
            INSERT INTO student_reg(uid,form_no,class_id,Registration_date,created_by,created_date,school_id,session,status,stutype)
            SELECT ?,?,?,GETDATE(),'admin',GETDATE(),?,?,?,1
            FROM student_reg WHERE school_id=? AND uid=?
            """, targetUid, formNo, target.get("classId"), targetSchool, targetSession, status, sourceSchool, sourceUid);
        jdbc.update("""
            INSERT INTO student_other_info
              (uid,school_id,Registration_NO,First_name,Mid_name,Last_name,childcat,DOB,Father_name,Mother_name,
               R_Address,Phone,Mobile,Gender,student_aadhar,Religion,RTE,FeeBookNo,BirthCertificate,TransferCertificate,
               transfer_status,Password,Student_id,login_status)
            SELECT ?,?,?,First_name,Mid_name,Last_name,childcat,DOB,Father_name,Mother_name,R_Address,Phone,Mobile,
                   Gender,student_aadhar,Religion,RTE,FeeBookNo,BirthCertificate,TransferCertificate,?,Registration_NO,?,1
            FROM student_other_info WHERE school_id=? AND uid=?
            """, targetUid, targetSchool, registration, "Transfer by " + sourceSchool, sourceRegistration,
            sourceSchool, sourceUid);
        if ("Registered".equals(status)) {
            jdbc.update("""
                INSERT INTO TB_StudentMaster
                  (uid,stu_id,Registration_NO,old_Registration_NO,House_id,Class_id,sec_id,Admission_date,Registration_date,
                   Withdrawn_date,Withdrawn_reason,status,stutype,quota,isnew,activity_id,readmission,school_id,session,
                   RollNo,FeeBookNo,Stream_id,Optional_sub,newstatus,old_house_id,Board_Reg_No,createddate)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,'Registered',1,'0','0','0','0',?,?, '', '', '', '', '', '', '', GETDATE())
                """, targetUid, targetStuId, registration, "", "", target.get("classId"), targetSection,
                new java.sql.Date(System.currentTimeMillis()), new java.sql.Date(System.currentTimeMillis()),
                "", "", targetSchool, targetSession);
        }
        return 1;
    }

    private Map<String, Object> student(String schoolId, String session, int sectionId, String student) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT TOP 1 sm.uid, sm.stu_id AS stuId, sm.registration_no AS registrationNo
            FROM TB_StudentMaster sm
            WHERE sm.school_id=? AND sm.session=? AND sm.sec_id=? AND LOWER(sm.status)='registered'
              AND (CAST(sm.stu_id AS varchar(100))=? OR sm.uid=?)
            ORDER BY sm.stu_id
            """, schoolId, session, sectionId, student, student);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private Map<String, Object> section(String schoolId, int sectionId) {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT TOP 1 sec_id AS sectionId, class_id AS classId FROM TB_Class WHERE school_id=? AND sec_id=?", schoolId, sectionId);
        if (rows.isEmpty()) throw new IllegalArgumentException("Destination section was not found");
        return rows.get(0);
    }

    private void createBasicLedger(String schoolId, String session, int sectionId, int studentId) {
        Map<String, Object> cls = section(schoolId, sectionId);
        List<Map<String, Object>> heads = jdbc.queryForList("""
            SELECT DISTINCT i.installment_no AS installmentNo, h.fee_head_id AS feeHeadId,
                   h.fee_type_id AS feeTypeId, COALESCE(ch.fee_charges,0) AS charge
            FROM Installment_Master i
            JOIN fee_head_master h ON h.school_id=i.school_id AND h.session=i.session AND h.fee_head_id=i.fee_head_id
            LEFT JOIN fee_head_charge ch ON ch.school_id=h.school_id AND ch.session=h.session AND ch.fee_head_id=h.fee_head_id
              AND ch.class_id=? AND ch.status=1
            WHERE i.school_id=? AND i.session=? AND i.status=1
            """, cls.get("classId"), schoolId, session);
        for (Map<String, Object> head : heads) {
            jdbc.update("""
                IF NOT EXISTS (SELECT 1 FROM fee_student_ledger WHERE school_id=? AND session=? AND stu_id=? AND installment_no=? AND fee_head_id=?)
                INSERT INTO fee_student_ledger(stu_id,installment_no,fee_head_id,fee_type_id,head_amount,concession,payable,receive_amount,balance,school_id,session,class_id)
                VALUES(?,?,?,?,?,0,?,0,?,?,?,?)
                """, schoolId, session, studentId, head.get("installmentNo"), head.get("feeHeadId"), studentId,
                head.get("installmentNo"), head.get("feeHeadId"), head.get("feeTypeId"), head.get("charge"), head.get("charge"),
                head.get("charge"), schoolId, session, cls.get("classId"));
        }
    }

    private String nextString(String column, String table, String schoolId) {
        if (!List.of("uid", "stu_id", "form_no").contains(column) || !List.of("student_reg", "TB_StudentMaster").contains(table)) {
            throw new IllegalArgumentException("Unsupported sequence column");
        }
        String sql = "SELECT CAST(ISNULL(MAX(TRY_CONVERT(bigint," + column + ")),0)+1 AS varchar(100)) FROM " + table + " WHERE school_id=?";
        return jdbc.queryForObject(sql, String.class, schoolId);
    }

    private int count(String sql, Object... args) {
        Integer value = jdbc.queryForObject(sql, Integer.class, args);
        return value == null ? 0 : value;
    }

    private static boolean blank(String value) { return value == null || value.isBlank(); }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value); }
}
