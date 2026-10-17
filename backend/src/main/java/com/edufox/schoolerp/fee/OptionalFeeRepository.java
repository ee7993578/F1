package com.edufox.schoolerp.fee;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

/** SQL access for the legacy AssignOptional workflow. No stored procedures are called. */
@Repository
@RequiredArgsConstructor
public class OptionalFeeRepository {
    private final JdbcTemplate jdbc;

    public Integer classIdForSection(String schoolId, int sectionId) {
        return jdbc.queryForObject("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", Integer.class, schoolId, sectionId);
    }

    public boolean hasCharge(String schoolId, String session, int feeHeadId, int classId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(1) FROM fee_head_charge WHERE school_id=? AND session=? AND fee_head_id=? AND class_id=? AND status=1", Integer.class, schoolId, session, feeHeadId, classId);
        return count != null && count > 0;
    }

    public List<Map<String, Object>> students(String schoolId, String session, int sectionId, String admissionNo, int feeHeadId) {
        StringBuilder sql = new StringBuilder("""
            SELECT st.stu_id AS id, st.stu_id AS studentId, st.registration_no AS admNo,
                   st.class_id AS classId,
                   UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')+
                       CASE WHEN COALESCE(ts.stream_name,'')='' THEN '' ELSE ' ['+ts.stream_name+']' END))) AS name,
                   CASE WHEN EXISTS (SELECT 1 FROM Assign_Optional_Fee a WHERE a.school_id=? AND a.session=? AND a.stu_id=st.stu_id AND a.fee_head_id=?) THEN CAST(1 AS bit) ELSE CAST(0 AS bit) END AS sts
            FROM TB_StudentMaster st
            LEFT JOIN student_other_info so ON so.registration_no=st.registration_no AND so.school_id=st.school_id
            LEFT JOIN tb_stream ts ON ts.stream_id=st.stream_id
            WHERE st.school_id=? AND st.session=? AND st.status='Registered' AND st.sec_id=?
            """);
        List<Object> args = new ArrayList<>(List.of(schoolId, session, feeHeadId, schoolId, session, sectionId));
        if (admissionNo != null && !admissionNo.isBlank()) { sql.append(" AND st.registration_no=?"); args.add(admissionNo.trim()); }
        sql.append(" ORDER BY so.first_name,so.mid_name,so.last_name,st.registration_no");
        List<Map<String,Object>> result = jdbc.queryForList(sql.toString(), args.toArray());
        for (Map<String,Object> student : result) {
            int studentId = Integer.parseInt(String.valueOf(student.get("studentId")));
            student.put("installments", jdbc.queryForList("SELECT DISTINCT installment_no AS value, CAST(installment_no AS varchar(20)) AS label FROM Installment_Master WHERE school_id=? AND session=? AND status=1 AND fee_type_id NOT IN (6,7) ORDER BY installment_no", schoolId, session));
            student.put("selectedInsts", jdbc.queryForList("SELECT DISTINCT CAST(installment_no AS varchar(20)) FROM fee_student_ledger WHERE school_id=? AND session=? AND fee_head_id=? AND stu_id=? ORDER BY CAST(installment_no AS varchar(20))", String.class, schoolId, session, feeHeadId, studentId));
        }
        return result;
    }

    public Integer feeType(String schoolId, String session, int feeHeadId) { return jdbc.queryForObject("SELECT TOP 1 TRY_CONVERT(int,fee_type_id) FROM fee_head_master WHERE school_id=? AND session=? AND fee_head_id=?", Integer.class, schoolId, session, feeHeadId); }
    public void applyStreamMapping(String schoolId, String session, int studentId, int feeHeadId) { List<Integer> ids = jdbc.queryForList("SELECT TOP 1 stream_id FROM tb_stream_mapping WHERE school_id=? AND session=? AND fee_head_id=?", Integer.class, schoolId, session, feeHeadId); if (!ids.isEmpty()) jdbc.update("UPDATE TB_StudentMaster SET stream_id=? WHERE stu_id=? AND session=? AND school_id=?", ids.get(0), studentId, session, schoolId); }
    public BigDecimal charge(String schoolId, String session, int feeHeadId, int classId, int feeType, int installment) { BigDecimal base = jdbc.queryForObject("SELECT TOP 1 CAST(fee_charges AS decimal(18,2)) FROM fee_head_charge WHERE fee_head_id=? AND class_id=? AND school_id=? AND session=? AND school_type=1 AND status=1", BigDecimal.class, feeHeadId, classId, schoolId, session); if (base == null) return BigDecimal.ZERO; if (feeType == 1) { Integer months = jdbc.queryForObject("SELECT COUNT(DISTINCT month_id) FROM Installment_Master WHERE installment_no=? AND session=? AND school_id=? AND fee_type_id=? AND status=1", Integer.class, installment, session, schoolId, feeType); return base.multiply(BigDecimal.valueOf(months == null ? 0 : months)); } return base; }
    public boolean assignmentExists(String schoolId, String session, int studentId, int feeHeadId, int installment) { Integer count = jdbc.queryForObject("SELECT COUNT(1) FROM Assign_Optional_Fee WHERE school_id=? AND session=? AND stu_id=? AND installment_no=? AND fee_head_id=?", Integer.class, schoolId, session, studentId, installment, feeHeadId); return count != null && count > 0; }
    public void insertAssignment(int studentId, int feeHeadId, int feeType, int installment, String staffUid, String schoolId, String session) { jdbc.update("INSERT INTO Assign_Optional_Fee(stu_id,fee_head_id,fee_type_id,installment_no,Assign_Date,create_by,create_date,school_id,session,Paid_status) VALUES(?,?,?,?,GETDATE(),?,GETDATE(),?,?,0)", studentId, feeHeadId, feeType, installment, staffUid, schoolId, session); }
    public void insertLedgerIfMissing(int studentId, int installment, int feeHeadId, BigDecimal charge, String schoolId, String session, int classId, int feeType) { jdbc.update("IF NOT EXISTS(SELECT 1 FROM fee_student_ledger WHERE fee_head_id=? AND school_id=? AND session=? AND stu_id=? AND installment_no=?) INSERT INTO fee_student_ledger(stu_id,installment_no,fee_head_id,head_amount,school_id,session,class_id,fee_type_id) VALUES(?,?,?,?,?,?,?,?)", feeHeadId, schoolId, session, studentId, installment, studentId, installment, feeHeadId, charge, schoolId, session, classId, feeType); }
    public int deleteUnpaidLedgerOutside(int studentId, String schoolId, String session, int feeHeadId, List<Integer> selected) { if (selected.isEmpty()) return jdbc.update("DELETE FROM fee_student_ledger WHERE stu_id=? AND school_id=? AND session=? AND fee_head_id=? AND receive_amount=0", studentId, schoolId, session, feeHeadId); String marks = String.join(",", Collections.nCopies(selected.size(), "?")); List<Object> args = new ArrayList<>(List.of(studentId, schoolId, session, feeHeadId)); args.addAll(selected); return jdbc.update("DELETE FROM fee_student_ledger WHERE stu_id=? AND school_id=? AND session=? AND fee_head_id=? AND receive_amount=0 AND installment_no NOT IN ("+marks+")", args.toArray()); }
    public int deleteUnpaidStudentRows(int studentId, String schoolId, String session, int feeHeadId) { int ledger = jdbc.update("DELETE FROM fee_student_ledger WHERE stu_id=? AND school_id=? AND session=? AND fee_head_id=? AND stu_id NOT IN (SELECT stu_id FROM stu_headwise_deposit WHERE school_id=? AND session=? AND fee_head_id=?)", studentId, schoolId, session, feeHeadId, schoolId, session, feeHeadId); int assignments = jdbc.update("DELETE FROM Assign_Optional_Fee WHERE stu_id=? AND school_id=? AND session=? AND fee_head_id=? AND stu_id NOT IN (SELECT stu_id FROM stu_headwise_deposit WHERE school_id=? AND session=? AND fee_head_id=?)", studentId, schoolId, session, feeHeadId, schoolId, session, feeHeadId); return ledger + assignments; }
}
