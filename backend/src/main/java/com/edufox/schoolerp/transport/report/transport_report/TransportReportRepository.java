package com.edufox.schoolerp.transport.report.transport_report;
import com.edufox.schoolerp.transport.work.student_transport.*;
import com.edufox.schoolerp.transport.work.transport.*;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class TransportReportRepository {
    private final JdbcTemplate jdbc;

    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }

    private static List<Integer> ids(String csv) {
        List<Integer> values = new ArrayList<>();
        for (String part : text(csv).split(",")) {
            try { if (!part.trim().isEmpty() && Integer.parseInt(part.trim()) > 0) values.add(Integer.parseInt(part.trim())); }
            catch (NumberFormatException ignored) { }
        }
        return values;
    }

    private static String in(List<Integer> values) { return String.join(",", Collections.nCopies(values.size(), "?")); }

    private static void addIn(StringBuilder sql, List<Object> args, String column, String csv) {
        List<Integer> values = ids(csv);
        if (!values.isEmpty()) { sql.append(" AND ").append(column).append(" IN (").append(in(values)).append(")"); args.addAll(values); }
    }

    public Map<String, Object> bootstrap(String schoolId, String session) {
        String selectedSession = text(session);
        if (selectedSession.isBlank() || "0".equals(selectedSession)) {
            List<String> current = jdbc.query("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC", (rs, row) -> rs.getString(1), schoolId);
            selectedSession = current.isEmpty() ? "" : text(current.get(0));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sessions", jdbc.queryForList("SELECT session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END,session DESC", schoolId));
        result.put("classes", jdbc.queryForList("SELECT c.sec_id AS id,c.class_id AS classId,cn.class_name+' ('+c.section+')' AS label,cn.class_name AS className,c.section FROM TB_Class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? ORDER BY c.class_id,c.sec_id", schoolId));
        result.put("routes", jdbc.queryForList("SELECT DISTINCT route_id AS id,route_name AS name,status FROM Route_Detail WHERE school_id=? AND status='1' ORDER BY route_name", schoolId));
        result.put("slabs", jdbc.queryForList("SELECT SlabID AS id,SlabName AS name FROM SlabMaster WHERE school_id=? ORDER BY SlabID", schoolId));
        result.put("months", jdbc.queryForList("SELECT month_id AS id,s_month AS name,s_month AS label FROM tb_month ORDER BY id"));
        result.put("installments", jdbc.queryForList("SELECT im.installment_no AS id,STUFF((SELECT ','+COALESCE(tm.month_name,'') FROM (SELECT DISTINCT month_id FROM Installment_Master WHERE school_id=im.school_id AND session=im.session AND fee_type_id=6 AND installment_no=im.installment_no) im2 LEFT JOIN tb_month tm ON tm.month_id=im2.month_id ORDER BY im2.month_id FOR XML PATH('')),1,1,'') AS label FROM Installment_Master im WHERE im.school_id=? AND im.session=? AND im.fee_type_id=6 GROUP BY im.installment_no,im.school_id,im.session ORDER BY im.installment_no", schoolId, selectedSession));
        result.put("session", selectedSession);
        result.put("schoolId", schoolId);
        return result;
    }

    public List<Map<String, Object>> assignedRoute(String schoolId, String session, String classId, String routeId) {
        String sql = "WITH latest AS (SELECT fa.*,ROW_NUMBER() OVER(PARTITION BY fa.school_id,fa.session,fa.stu_id ORDER BY fa.id DESC) rn FROM fee_Assign_Transport fa WHERE fa.school_id=? AND fa.session=?) " +
                "SELECT ROW_NUMBER() OVER(ORDER BY st.class_id,st.sec_id,so.first_name,st.registration_no) AS sno,st.registration_no AS registrationNo," +
                "UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName," +
                "cn.class_name+' '+cl.section AS className,COALESCE(fh.fee_head,'') AS routeName,COALESCE(rd.route_name,'') AS routeDetail," +
                "COALESCE(v.vehicle_no,'') AS vehicleNo,COALESCE(tt.name,'') AS transportType,COALESCE(d.drivername,'') AS driverName " +
                "FROM TB_StudentMaster st JOIN student_other_info so ON so.uid=st.uid AND so.registration_no=st.registration_no AND so.school_id=st.school_id " +
                "JOIN TB_Class cl ON cl.sec_id=st.sec_id AND cl.school_id=st.school_id JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id " +
                "JOIN latest fa ON fa.school_id=st.school_id AND fa.session=st.session AND fa.stu_id=st.stu_id AND fa.rn=1 " +
                "LEFT JOIN fee_head_master fh ON fh.school_id=st.school_id AND fh.session=st.session AND fh.fee_head_id=fa.stoppage_id " +
                "LEFT JOIN Route_Detail rd ON rd.school_id=fa.school_id AND rd.route_id=fa.route_id " +
                "LEFT JOIN vehicle_master v ON v.school_id=fa.school_id AND v.vehicleno_id=fa.vehicleNO AND v.transport_type_id=fa.transport_type_id " +
                "LEFT JOIN transport_type tt ON tt.school_id=fa.school_id AND tt.transport_type_id=fa.transport_type_id " +
                "LEFT JOIN driver_vehicle_detail d ON d.school_id=fa.school_id AND d.route_id=fa.route_id AND d.transport_type_id=fa.transport_type_id AND d.vehicleno_id=fa.vehicleNO " +
                "WHERE st.school_id=? AND st.session=? AND LOWER(st.status)='registered'";
        List<Object> args = new ArrayList<>(List.of(schoolId, session, schoolId, session));
        if (!text(classId).isBlank() && !"0".equals(classId)) { sql += " AND st.sec_id=?"; args.add(Integer.parseInt(classId)); }
        if (!text(routeId).isBlank() && !"0".equals(routeId)) { sql += " AND fa.route_id=?"; args.add(Integer.parseInt(routeId)); }
        return jdbc.queryForList(sql + " ORDER BY st.class_id,st.sec_id,studentName,st.registration_no", args.toArray());
    }

    public List<Map<String, Object>> student(String schoolId, String session, String classIds, String routeIds, String slabId) {
        String sql = "WITH latest AS (SELECT fa.*,ROW_NUMBER() OVER(PARTITION BY fa.school_id,fa.session,fa.stu_id ORDER BY fa.id DESC) rn FROM fee_Assign_Transport fa WHERE fa.school_id=? AND fa.session=?) " +
                "SELECT st.registration_no AS admissionNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mobile,'') AS mobile," +
                "cn.class_name+' ('+cl.section+')' AS className,COALESCE(rd.route_name,'') AS routeName,COALESCE(fh.fee_head,'') AS stoppageName,COALESCE(sm.SlabName,'') AS slabName " +
                "FROM TB_StudentMaster st JOIN student_other_info so ON so.uid=st.uid AND so.registration_no=st.registration_no AND so.school_id=st.school_id " +
                "JOIN TB_Class cl ON cl.sec_id=st.sec_id AND cl.school_id=st.school_id JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id JOIN latest fa ON fa.school_id=st.school_id AND fa.session=st.session AND fa.stu_id=st.stu_id AND fa.rn=1 " +
                "LEFT JOIN Route_Detail rd ON rd.school_id=fa.school_id AND rd.route_id=fa.route_id LEFT JOIN fee_head_master fh ON fh.school_id=fa.school_id AND fh.session=st.session AND fh.fee_head_id=fa.stoppage_id " +
                "LEFT JOIN stoppage_detail sd ON sd.school_id=fa.school_id AND sd.stoppage_id=fa.stoppage_id LEFT JOIN SlabMaster sm ON sm.school_id=sd.school_id AND sm.SlabID=sd.SlabId WHERE st.school_id=? AND st.session=? AND LOWER(st.status)='registered'";
        List<Object> args = new ArrayList<>(List.of(schoolId, session, schoolId, session));
        StringBuilder q = new StringBuilder(sql); addIn(q, args, "st.sec_id", classIds); addIn(q, args, "fa.route_id", routeIds);
        if (!text(slabId).isBlank() && !"0".equals(slabId)) { q.append(" AND sd.SlabId=?"); args.add(Integer.parseInt(slabId)); }
        return jdbc.queryForList(q + " ORDER BY st.class_id,st.sec_id,studentName,st.registration_no", args.toArray());
    }

    public List<Map<String, Object>> privateStudents(String schoolId, String session, String classIds) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT st.sec_id AS secId,st.class_id AS classId,st.stu_id AS studentId,st.registration_no AS admissionNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mobile,'') AS mobile,COALESCE(so.r_address,'') AS address,cn.class_name+'('+cl.section+')' AS className FROM TB_StudentMaster st JOIN student_other_info so ON so.uid=st.uid AND so.registration_no=st.registration_no AND so.school_id=st.school_id JOIN TB_Class cl ON cl.sec_id=st.sec_id AND cl.school_id=st.school_id JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id WHERE LOWER(st.status)='registered' AND st.school_id=? AND st.session=? AND st.stu_id NOT IN (SELECT stu_id FROM fee_Assign_Transport_Mapping WHERE school_id=? AND month_id=MONTH(GETDATE()))");
        List<Object> args = new ArrayList<>(List.of(schoolId, session, schoolId)); addIn(sql, args, "st.sec_id", classIds);
        return jdbc.queryForList(sql + " ORDER BY st.class_id,st.sec_id,studentName", args.toArray());
    }

    public Map<String, Object> monthwise(String schoolId, String session, String classIds, String monthId, String routeId, String studentStatus, String receiveAmount) {
        StringBuilder sql = new StringBuilder("SELECT m.stu_id AS studentId,m.month_id AS monthId,m.installment_no AS installmentNo,m.stoppage_id AS stoppageId,COALESCE(rd.route_name,'') AS routeName,st.registration_no AS admissionNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,cn.class_name+' '+cl.section AS className,cl.class_id AS classId,cl.sec_id AS secId,COALESCE(l.payable,m.charges,0) AS dueAmount,COALESCE(d.receivedAmount,0) AS paidAmount FROM fee_Assign_Transport_Mapping m JOIN fee_Assign_Transport fa ON fa.assign_id=m.assign_id AND fa.school_id=m.school_id AND fa.session=? JOIN TB_StudentMaster st ON st.stu_id=m.stu_id AND st.school_id=m.school_id AND st.session=fa.session JOIN student_other_info so ON so.registration_no=st.registration_no AND so.school_id=st.school_id JOIN TB_Class cl ON cl.sec_id=st.sec_id AND cl.school_id=st.school_id JOIN class_name cn ON cn.id=st.class_id AND cn.school_id=st.school_id LEFT JOIN Route_Detail rd ON rd.school_id=fa.school_id AND rd.route_id=fa.route_id LEFT JOIN fee_student_ledger l ON l.school_id=m.school_id AND l.session=fa.session AND l.stu_id=m.stu_id AND l.installment_no=m.installment_no AND l.fee_head_id=m.stoppage_id AND l.fee_type_id=6 LEFT JOIN (SELECT school_id,stu_id,inst_no,fee_head_id,SUM(COALESCE(receive_amt,0)) receivedAmount FROM stu_headwise_deposit WHERE LOWER(status)='active' AND fee_type_id=6 GROUP BY school_id,stu_id,inst_no,fee_head_id) d ON d.school_id=m.school_id AND d.stu_id=m.stu_id AND d.inst_no=m.installment_no AND d.fee_head_id=m.stoppage_id WHERE m.school_id=?");
        List<Object> args = new ArrayList<>(List.of(session, schoolId)); addIn(sql, args, "st.sec_id", classIds);
        if (!text(monthId).isBlank() && !"0".equals(monthId)) { sql.append(" AND m.month_id=?"); args.add(Integer.parseInt(monthId)); }
        if (!text(routeId).isBlank() && !"0".equals(routeId)) { sql.append(" AND fa.route_id=?"); args.add(Integer.parseInt(routeId)); }
        if (!text(studentStatus).isBlank() && !"0".equals(studentStatus)) { sql.append(" AND LOWER(st.status)=LOWER(?)"); args.add(studentStatus); }
        List<Map<String, Object>> raw = jdbc.queryForList(sql + " ORDER BY st.class_id,st.sec_id,studentName,m.month_id", args.toArray());
        List<Map<String, Object>> monthRows = jdbc.queryForList("SELECT month_id AS id,s_month AS name FROM tb_month " + ((!text(monthId).isBlank() && !"0".equals(monthId)) ? "WHERE month_id=? " : "") + "ORDER BY id", (!text(monthId).isBlank() && !"0".equals(monthId)) ? new Object[]{Integer.parseInt(monthId)} : new Object[]{});
        List<Map<String, String>> columns = new ArrayList<>(); columns.add(Map.of("key", "admissionNo", "label", "Adm no")); columns.add(Map.of("key", "studentName", "label", "Student")); columns.add(Map.of("key", "className", "label", "Class"));
        LinkedHashMap<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> row : raw) {
            String student = text(row.get("studentId")); Map<String, Object> out = grouped.computeIfAbsent(student, k -> new LinkedHashMap<>());
            if (out.isEmpty()) { out.put("studentId", row.get("studentId")); out.put("admissionNo", row.get("admissionNo")); out.put("studentName", row.get("studentName")); out.put("className", row.get("className")); }
            String month = text(row.get("monthId")); out.put("stoppage" + month, row.get("routeName"));
            if ("1".equals(receiveAmount)) out.put("due" + month, row.get("dueAmount"));
            if ("2".equals(receiveAmount)) out.put("paid" + month, row.get("paidAmount"));
        }
        for (Map<String, Object> month : monthRows) { String id = text(month.get("id")); String name = text(month.get("name")); columns.add(Map.of("key", "stoppage" + id, "label", name)); if ("1".equals(receiveAmount)) columns.add(Map.of("key", "due" + id, "label", "Due Amount")); if ("2".equals(receiveAmount)) columns.add(Map.of("key", "paid" + id, "label", "Paid Amount")); }
        return Map.of("columns", columns, "rows", new ArrayList<>(grouped.values()));
    }

    public List<Map<String, Object>> routeCollection(String schoolId, String session, String routeId, String installment, String classId, String category) {
        StringBuilder sql = new StringBuilder("WITH eligible AS (SELECT DISTINCT fa.school_id,fa.session,fa.stu_id FROM fee_Assign_Transport fa WHERE fa.school_id=? AND fa.session=?), selected AS (SELECT l.stu_id,l.installment_no,l.fee_head_id,l.school_id,l.session,l.payable,l.receive_amount,l.balance FROM fee_student_ledger l JOIN eligible e ON e.school_id=l.school_id AND e.session=l.session AND e.stu_id=l.stu_id WHERE l.school_id=? AND l.session=? AND l.fee_type_id=6) SELECT l.stu_id AS studentId,st.registration_no AS admissionNo,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,cn.class_name+'('+cl.section+')' AS className,COALESCE(d.paymentDate,'') AS date,COALESCE(fh.fee_head,'') AS stoppage,COALESCE(rd.route_name,'') AS routeName,COALESCE(l.payable,0) AS payable,COALESCE(l.receive_amount,0) AS receiveAmount,COALESCE(l.balance,0) AS balance,COALESCE(so.phone,'') AS phone,COALESCE(so.mobile,'') AS mobile FROM selected l JOIN TB_StudentMaster st ON st.school_id=l.school_id AND st.stu_id=l.stu_id AND st.session=l.session AND LOWER(st.status)='registered' JOIN student_other_info so ON so.school_id=st.school_id AND so.registration_no=st.registration_no JOIN TB_Class cl ON cl.school_id=st.school_id AND cl.sec_id=st.sec_id JOIN class_name cn ON cn.school_id=st.school_id AND cn.id=st.class_id OUTER APPLY (SELECT TOP 1 fa.school_id,fa.route_id FROM fee_Assign_Transport fa WHERE fa.school_id=l.school_id AND fa.session=l.session AND fa.stu_id=l.stu_id ORDER BY fa.id DESC) fa LEFT JOIN fee_head_master fh ON fh.school_id=l.school_id AND fh.session=l.session AND fh.fee_head_id=l.fee_head_id LEFT JOIN Route_Detail rd ON rd.school_id=fa.school_id AND rd.route_id=fa.route_id OUTER APPLY (SELECT TOP 1 CONVERT(varchar(70),p.paymentdate,106) AS paymentDate FROM fee_payment_detail p WHERE p.school_id=l.school_id AND p.financial_year=l.session AND p.uid IN (SELECT h.uid FROM stu_headwise_deposit h WHERE h.school_id=l.school_id AND h.stu_id=l.stu_id AND h.inst_no=l.installment_no AND h.fee_head_id=l.fee_head_id)) d WHERE l.school_id=? AND l.session=?");
        List<Object> args = new ArrayList<>(List.of(schoolId, session, schoolId, session, schoolId, session));
        if (!text(installment).isBlank() && !"0".equals(installment)) { sql.append(" AND l.installment_no=?"); args.add(Integer.parseInt(installment)); }
        if (!text(routeId).isBlank() && !"0".equals(routeId)) { sql.append(" AND EXISTS (SELECT 1 FROM fee_Assign_Transport rf WHERE rf.school_id=l.school_id AND rf.session=l.session AND rf.stu_id=l.stu_id AND rf.route_id=? )"); args.add(Integer.parseInt(routeId)); }
        if (!text(classId).isBlank() && !"0".equals(classId)) { sql.append(" AND st.sec_id=?"); args.add(Integer.parseInt(classId)); }
        if ("1".equals(category)) sql.append(" AND COALESCE(l.balance,0)<>0"); else if ("2".equals(category)) sql.append(" AND COALESCE(l.balance,0)=0");
        return jdbc.queryForList(sql + " ORDER BY st.class_id,st.sec_id,studentName,l.installment_no,l.fee_head_id", args.toArray());
    }

    public List<Map<String, Object>> routeCollectionSummary(String schoolId, String session, String installment) {
        List<Map<String, Object>> detail = routeCollection(schoolId, session, "0", installment, "0", "0");
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();
        Map<String, Set<String>> students = new HashMap<>();
        for (Map<String, Object> row : detail) {
            String route = text(row.get("routeName"));
            Map<String, Object> out = grouped.computeIfAbsent(route, key -> {
                Map<String, Object> value = new LinkedHashMap<>(); value.put("routeName", key); value.put("payable", 0D); value.put("receiveAmount", 0D); value.put("balance", 0D); value.put("students", 0); return value;
            });
            out.put("payable", ((Number) out.get("payable")).doubleValue() + number(row.get("payable")));
            out.put("receiveAmount", ((Number) out.get("receiveAmount")).doubleValue() + number(row.get("receiveAmount")));
            out.put("balance", ((Number) out.get("balance")).doubleValue() + number(row.get("balance")));
            students.computeIfAbsent(route, key -> new HashSet<>()).add(text(row.get("studentId")));
        }
        int sno = 1;
        for (Map<String, Object> row : grouped.values()) row.put("sno", sno++);
        for (Map.Entry<String, Map<String, Object>> entry : grouped.entrySet()) entry.getValue().put("studentCount", students.getOrDefault(entry.getKey(), Set.of()).size());
        return new ArrayList<>(grouped.values());
    }

    private static double number(Object value) { try { return value == null ? 0D : Double.parseDouble(String.valueOf(value)); } catch (NumberFormatException ignored) { return 0D; } }
}
