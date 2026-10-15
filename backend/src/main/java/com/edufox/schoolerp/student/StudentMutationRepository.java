package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StudentMutationRepository {
    private final JdbcTemplate jdbc;

    @Transactional
    public StudentMutationDtos.MutationResponse update(String schoolId, String uid, StudentMutationDtos.UpdateRequest request) {
        List<String> regSet = new ArrayList<>();
        List<Object> regArgs = new ArrayList<>();
        add(regSet, regArgs, "form_no", request.registrationNo());
        add(regSet, regArgs, "first_name", request.firstName());
        add(regSet, regArgs, "mid_name", request.middleName());
        add(regSet, regArgs, "last_name", request.lastName());
        add(regSet, regArgs, "dob", request.dob());
        add(regSet, regArgs, "gender", request.gender());
        add(regSet, regArgs, "blood_group", request.bloodGroup());
        add(regSet, regArgs, "child_cat", request.category());
        add(regSet, regArgs, "quota", request.quota());
        add(regSet, regArgs, "father_name", request.fatherName());
        add(regSet, regArgs, "mother_name", request.motherName());
        add(regSet, regArgs, "r_address", request.address());
        add(regSet, regArgs, "phone", request.phone());
        add(regSet, regArgs, "mobile", request.mobile());
        add(regSet, regArgs, "student_aadhar", request.studentAadhar());
        add(regSet, regArgs, "religion", request.religion());
        add(regSet, regArgs, "status", request.status());
        add(regSet, regArgs, "session", request.session());
        if (!regSet.isEmpty()) {
            regArgs.add(uid); regArgs.add(schoolId);
            jdbc.update("UPDATE student_reg SET " + String.join(", ", regSet) + " WHERE uid=? AND school_id=?", regArgs.toArray());
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
        if (!otherSet.isEmpty()) {
            otherArgs.add(uid); otherArgs.add(schoolId);
            jdbc.update("UPDATE student_other_info SET " + String.join(", ", otherSet) + " WHERE uid=? AND school_id=?", otherArgs.toArray());
        }

        List<Object> masterArgs = new ArrayList<>();
        List<String> masterSet = new ArrayList<>();
        add(masterSet, masterArgs, "registration_no", request.registrationNo());
        add(masterSet, masterArgs, "status", request.status());
        add(masterSet, masterArgs, "session", request.session());
        if (request.status() != null) {
            masterSet.add("Withdrawn_date=CASE WHEN ? <> 'Registered' THEN GETDATE() ELSE NULL END");
            masterArgs.add(request.status().trim());
        }
        add(masterSet, masterArgs, "Withdrawn_reason", request.remark());
        if (request.sectionId() != null) { masterSet.add("sec_id=?"); masterArgs.add(request.sectionId()); }
        if (request.className() != null && !request.className().isBlank()) {
            Integer classId = findClassId(schoolId, request.className());
            if (classId != null) { masterSet.add("class_id=?"); masterArgs.add(classId); }
        }
        if (!masterSet.isEmpty()) {
            masterArgs.add(uid); masterArgs.add(schoolId);
            jdbc.update("UPDATE TB_StudentMaster SET " + String.join(", ", masterSet) + " WHERE uid=? AND school_id=?", masterArgs.toArray());
        }

        return new StudentMutationDtos.MutationResponse(uid, "Student record updated");
    }

    @Transactional
    public StudentMutationDtos.MutationResponse softDelete(String schoolId, String uid) {
        jdbc.update("UPDATE student_reg SET status='Deleted' WHERE uid=? AND school_id=?", uid, schoolId);
        jdbc.update("UPDATE TB_StudentMaster SET status='Deleted' WHERE uid=? AND school_id=?", uid, schoolId);
        return new StudentMutationDtos.MutationResponse(uid, "Student record deleted");
    }

    private Integer findClassId(String schoolId, String className) {
        try {
            return jdbc.queryForObject("SELECT TOP 1 id FROM class_name WHERE school_id=? AND class_name=? ORDER BY id", Integer.class, schoolId, className);
        } catch (Exception ex) {
            try { return Integer.valueOf(className); } catch (NumberFormatException ignored) { return null; }
        }
    }

    private static void add(List<String> set, List<Object> args, String column, String value) {
        if (value != null) { set.add(column + "=?"); args.add(value.trim()); }
    }
}