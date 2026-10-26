package com.edufox.schoolerp.hostel.work.hostel;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class HostelRepository {
    private final JdbcTemplate jdbc;

    private static String s(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static int i(Object value) { return s(value).isBlank() ? 0 : Integer.parseInt(s(value)); }
    private static double d(Object value) { return s(value).isBlank() ? 0D : Double.parseDouble(s(value)); }
    private static Map<String, Object> row(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int n = 0; n + 1 < values.length; n += 2) result.put(String.valueOf(values[n]), values[n + 1]);
        return result;
    }

    public String currentSession(String school) {
        List<String> rows = jdbc.query("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END,session DESC",
            (rs, n) -> rs.getString(1), school);
        return rows.isEmpty() ? "" : s(rows.get(0));
    }

    public Map<String, Object> bootstrap(String school) {
        String session = currentSession(school);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("session", session);
        result.put("sessions", jdbc.queryForList("SELECT session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END,session DESC", school));
        result.put("classes", jdbc.queryForList("SELECT b.Sec_id AS id,b.class_id AS classId,a.Class_name+' ('+b.Section+')' AS label,a.Class_name AS className,b.Section AS section FROM class_name a JOIN TB_Class b ON a.id=b.class_id AND a.school_id=b.school_id WHERE a.school_id=? ORDER BY b.class_id,b.Sec_id", school));
        result.put("installments", installments(school, session));
        result.put("records", feeRecords(school, session));
        return result;
    }

    public List<Map<String, Object>> installments(String school, String session) {
        return jdbc.queryForList("SELECT DISTINCT installment_no AS installmentNo,CONVERT(varchar(50),due_date,106) AS dueDate FROM Installment_Master WHERE fee_type_id=7 AND school_id=? AND session=? AND status=1 ORDER BY installment_no", school, session);
    }

    public List<Map<String, Object>> feeRecords(String school, String session) {
        return jdbc.queryForList("SELECT h.hostel_installment_no AS installmentNo,'Hostel Fee' AS feeType,COALESCE(STUFF((SELECT ', '+COALESCE(m.month_name,'') FROM Installment_Master ix LEFT JOIN tb_month m ON m.month_id=ix.month_id WHERE ix.school_id=h.school_id AND ix.session=h.session AND ix.fee_type_id=7 AND ix.installment_no=h.hostel_installment_no ORDER BY ix.month_id FOR XML PATH('')),1,2,''),'') AS months,CONVERT(varchar(50),MAX(ix.due_date),106) AS dueDate,h.installment_charges AS amount FROM hostel_fee_master h LEFT JOIN Installment_Master ix ON ix.school_id=h.school_id AND ix.session=h.session AND ix.fee_type_id=7 AND ix.installment_no=h.hostel_installment_no WHERE h.school_id=? AND h.session=? AND ISNULL(h.status,1)=1 GROUP BY h.hostel_installment_no,h.school_id,h.session,h.installment_charges ORDER BY h.hostel_installment_no", school, session);
    }

    public List<Map<String, Object>> students(String school, String session, String classId, String admissionNo) {
        StringBuilder sql = new StringBuilder("SELECT st.stu_id AS studentId,st.Registration_NO AS admissionNo,UPPER(LTRIM(RTRIM(COALESCE(so.First_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.Father_name,'') AS fatherName,cn.class_name+' ('+cl.Section+')' AS className,st.class_id AS classId,st.sec_id AS secId,CASE WHEN hs.status='0' THEN 'w' ELSE COALESCE(hs.status,'0') END AS status FROM TB_StudentMaster st JOIN student_other_info so ON st.uid=so.uid AND st.Registration_NO=so.Registration_NO AND st.school_id=so.school_id JOIN TB_Class cl ON st.sec_id=cl.sec_id AND st.school_id=cl.school_id JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id OUTER APPLY (SELECT TOP 1 fa.status FROM fee_Assign_Hostel fa WHERE fa.stu_id=st.stu_id AND fa.school_id=? AND fa.session=? ORDER BY fa.id DESC) hs WHERE st.status='Registered' AND st.school_id=? AND st.session=?");
        List<Object> params = new ArrayList<>(List.of(school, session, school, session));
        if (!s(classId).isBlank() && !"0".equals(classId)) { sql.append(" AND st.sec_id=?"); params.add(classId); }
        if (!s(admissionNo).isBlank() && !"0".equals(admissionNo)) { sql.append(" AND st.Registration_NO=?"); params.add(admissionNo); }
        sql.append(" ORDER BY cn.class_order,st.Registration_NO");
        return jdbc.queryForList(sql.toString(), params.toArray());
    }

    public List<Map<String, Object>> assignments(String school, String session, String classId, String search) {
        StringBuilder sql = new StringBuilder("SELECT fa.id AS assignmentId,fa.stu_id AS studentId,st.Registration_NO AS admissionNo,UPPER(LTRIM(RTRIM(COALESCE(so.First_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.Father_name,'') AS fatherName,cn.class_name+' ('+cl.Section+')' AS className,fa.assign_date AS assignDate,fa.status,fa.created_by AS createdBy FROM fee_Assign_Hostel fa JOIN TB_StudentMaster st ON st.stu_id=fa.stu_id AND st.school_id=fa.school_id JOIN student_other_info so ON st.uid=so.uid AND st.Registration_NO=so.Registration_NO AND st.school_id=so.school_id JOIN TB_Class cl ON st.sec_id=cl.sec_id AND st.school_id=cl.school_id JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id WHERE fa.school_id=? AND fa.session=? AND ISNULL(fa.status,'1')='1'");
        List<Object> params = new ArrayList<>(List.of(school, session));
        if (!s(classId).isBlank() && !"0".equals(classId)) { sql.append(" AND st.sec_id=?"); params.add(classId); }
        if (!s(search).isBlank()) { sql.append(" AND (st.Registration_NO LIKE ? OR so.First_name+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'') LIKE ? OR so.Father_name LIKE ? OR cn.class_name+' ('+cl.Section+')' LIKE ?)"); String q="%"+search.trim()+"%"; params.add(q); params.add(q); params.add(q); params.add(q); }
        sql.append(" ORDER BY cn.class_order,st.Registration_NO");
        return jdbc.queryForList(sql.toString(), params.toArray());
    }

    public Map<String, Object> saveAssignments(String school, String session, String staff, List<?> rawStudents) {
        int saved = 0;
        if (rawStudents != null) for (Object raw : rawStudents) {
            if (!(raw instanceof Map<?, ?> item) || !Boolean.parseBoolean(s(item.get("selected")))) continue;
            int studentId = i(item.get("studentId"));
            if (studentId == 0) continue;
            Integer exists = jdbc.queryForObject("SELECT COUNT(1) FROM fee_Assign_Hostel WHERE stu_id=? AND school_id=? AND session=?", Integer.class, studentId, school, session);
            if (exists == null || exists == 0) {
                jdbc.update("INSERT INTO fee_Assign_Hostel(stu_id,assign_date,withdrawn_date,reason,status,school_id,session,created_date,created_by,modified_date,modified_by) VALUES(?,GETDATE(),NULL,'',1,?,?,GETDATE(),?,NULL,NULL)", studentId, school, session, staff);
                createLedger(school, session, studentId);
                saved++;
            }
        }
        return row("message", "Record Saved", "saved", saved);
    }

    private void createLedger(String school, String session, int studentId) {
        jdbc.update("INSERT INTO fee_student_ledger(stu_id,installment_no,fee_head_id,fee_type_id,head_amount,concession,payable,receive_amount,balance,school_id,session,class_id) SELECT ?,i.installment_no,h.fee_head_id,7,COALESCE(c.fee_charges,0),0,COALESCE(c.fee_charges,0),0,COALESCE(c.fee_charges,0),?,?,st.class_id FROM (SELECT DISTINCT installment_no FROM Installment_Master WHERE school_id=? AND session=? AND fee_type_id=7 AND status=1) i JOIN fee_head_master h ON h.school_id=? AND h.session=? AND h.fee_type_id=7 AND h.status=1 LEFT JOIN (SELECT fee_head_id,MAX(fee_charges) AS fee_charges FROM fee_head_charge WHERE school_id=? AND session=? AND fee_type_id=7 AND status=1 GROUP BY fee_head_id) c ON c.fee_head_id=h.fee_head_id JOIN TB_StudentMaster st ON st.stu_id=? AND st.school_id=? WHERE NOT EXISTS(SELECT 1 FROM fee_student_ledger x WHERE x.stu_id=? AND x.installment_no=i.installment_no AND x.fee_head_id=h.fee_head_id AND x.school_id=? AND x.session=?)", studentId, school, session, school, session, school, session, studentId, school, studentId, school, session);
    }

    public Map<String, Object> saveFees(String school, String session, String staff, List<?> rawRows) {
        int saved = 0;
        if (rawRows != null) for (Object raw : rawRows) {
            if (!(raw instanceof Map<?, ?> item)) continue;
            int installment = i(item.get("installmentNo")); double amount = d(item.get("amount"));
            if (installment == 0 || amount <= 0) continue;
            Integer exists = jdbc.queryForObject("SELECT COUNT(1) FROM hostel_fee_master WHERE hostel_installment_no=? AND school_id=? AND session=?", Integer.class, installment, school, session);
            if (exists == null || exists == 0) jdbc.update("INSERT INTO hostel_fee_master(hostel_installment_no,installment_charges,status,created_date,created_by,school_id,session) VALUES(?,?,1,GETDATE(),?,?,?)", installment, amount, staff, school, session);
            Integer feeHead = jdbc.queryForObject("SELECT TOP 1 fee_head_id FROM fee_head_master WHERE fee_head=? AND fee_type_id=7 AND school_id=? AND session=? ORDER BY row_id DESC", Integer.class, "Hostel" + installment, school, session);
            if (feeHead == null) {
                feeHead = jdbc.queryForObject("SELECT ISNULL(MAX(fee_head_id),0)+1 FROM fee_head_master", Integer.class);
                Integer order = jdbc.queryForObject("SELECT ISNULL(MAX([order]),0)+1 FROM fee_head_master WHERE school_id=? AND session=?", Integer.class, school, session);
                jdbc.update("INSERT INTO fee_head_master(fee_head_id,fee_type_id,fee_head,fee_code,[order],status,created_date,created_by,modified_date,modified_by,school_id,session,isOptional,sub_id) VALUES(?,?,?,?,?,1,GETDATE(),?,GETDATE(),?,?,?,1,0)", feeHead, 7, "Hostel" + installment, "hos" + installment, order == null ? 1 : order, staff, "", school, session);
            } else {
                jdbc.update("UPDATE fee_head_master SET status=1,modified_by=?,modified_date=GETDATE() WHERE fee_head_id=? AND school_id=? AND session=?", staff, feeHead, school, session);
            }
            Integer charge = jdbc.queryForObject("SELECT COUNT(1) FROM fee_head_charge WHERE fee_head_id=? AND fee_type_id=7 AND class_id=0 AND school_id=? AND session=?", Integer.class, feeHead, school, session);
            if (charge != null && charge > 0) jdbc.update("UPDATE fee_head_charge SET fee_charges=?,status=1,modified_by=?,modified_date=GETDATE() WHERE fee_head_id=? AND fee_type_id=7 AND class_id=0 AND school_id=? AND session=?", amount, staff, feeHead, school, session);
            else jdbc.update("INSERT INTO fee_head_charge(fee_head_id,fee_type_id,fee_charges,class_id,status,created_date,created_by,modified_date,modified_by,school_id,session,school_type,transport_type_id) VALUES(?,?,?,0,1,GETDATE(),?,GETDATE(),?,?,?,1,0)", feeHead, 7, amount, staff, "", school, session);
            saved++;
        }
        return row("message", "Record Saved", "saved", saved);
    }
}
