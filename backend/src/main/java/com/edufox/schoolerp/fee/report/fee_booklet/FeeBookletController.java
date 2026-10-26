package com.edufox.schoolerp.fee.report.fee_booklet;

import com.edufox.schoolerp.common.persistence.SessionResolver;
import com.edufox.schoolerp.fee.report.fee_reports.*;
import com.edufox.schoolerp.fee.shared.fee_core.*;
import com.edufox.schoolerp.fee.update.fee_update.*;
import com.edufox.schoolerp.fee.update.optional_fee.*;
import com.edufox.schoolerp.fee.work.fee_work.*;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/** Direct Java/read-only replacement for the legacy fee_booklet.aspx flow. */
@RestController
@RequestMapping("/api/fees/booklet")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class FeeBookletController {
    private final JdbcTemplate jdbc;

    private static Claims claims(Authentication a){return (Claims)a.getDetails();}
    private static String school(Authentication a){return text(claims(a).get("schoolId",String.class));}
    private static String text(Object v){return v==null?"":String.valueOf(v).trim();}
    private String session(Object requested,String sc){return SessionResolver.resolve(jdbc, requested, sc);}

    @GetMapping("/bootstrap")
    public Map<String,Object> bootstrap(@RequestParam(required=false)String session,Authentication a){
        String sc=school(a),ses=session(session,sc);
        Map<String,Object> result=new LinkedHashMap<>();result.put("session",ses);
        result.put("sessions",jdbc.queryForList("SELECT id,session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY id DESC",sc));
        result.put("classes",jdbc.queryForList("SELECT c.sec_id AS id,c.class_id AS classId,cn.class_name AS className,c.section,cn.class_name+' ('+c.section+')' AS label FROM TB_Class c JOIN class_name cn ON cn.id=c.class_id AND cn.school_id=c.school_id WHERE c.school_id=? ORDER BY c.class_id,c.section",sc));
        result.put("installments",jdbc.queryForList("SELECT DISTINCT installment_no AS value,installment_no AS label FROM Installment_Master WHERE school_id=? AND session=? ORDER BY installment_no",sc,ses));
        result.put("school",jdbc.queryForList("SELECT TOP 1 schoolname,web_address,logo_path,schoolname,bank_branch,bank_acc,bank_name,tc_add1,tc_add2,tc_add3,tc_add4,phone,mobile,IFSC FROM schoolmaster WHERE school_id=?",sc));
        return result;
    }

    @GetMapping("/students")
    public List<Map<String,Object>> students(@RequestParam(required=false)String session,@RequestParam(required=false)Integer secId,@RequestParam(defaultValue="2")String isNew,@RequestParam(defaultValue="0")String rte,@RequestParam(required=false)String query,Authentication a){
        String sc=school(a),ses=session(session,sc);StringBuilder q=new StringBuilder("SELECT sm.stu_id AS id,sm.stu_id AS studentId,sm.registration_no AS admissionNo,sm.FeeBookNo AS feeBookNo,sm.isnew,COALESCE(so.RTE,0) AS rte,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''))))+' ['+sm.registration_no+']' AS name,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.father_name,'') AS fatherName,sm.sec_id AS secId,sm.class_id AS classId FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid WHERE sm.school_id=? AND sm.session=? AND sm.status='registered'");List<Object> p=new ArrayList<>(List.of(sc,ses));
        if(secId!=null){q.append(" AND sm.sec_id=?");p.add(secId);}if(!text(isNew).isBlank()&&!"2".equals(isNew)){q.append(" AND sm.isnew=?");p.add("1".equals(isNew)?1:0);}q.append(" AND COALESCE(so.RTE,0)=?");p.add("1".equals(rte)?1:0);if(!text(query).isBlank()){q.append(" AND (sm.registration_no LIKE ? OR so.first_name LIKE ? OR so.last_name LIKE ? OR so.father_name LIKE ?)");String v="%"+text(query)+"%";p.addAll(List.of(v,v,v,v));}q.append(" ORDER BY so.first_name,so.mid_name,so.last_name,sm.stu_id");return jdbc.queryForList(q.toString(),p.toArray());
    }

    @GetMapping("/preview")
    public Map<String,Object> preview(@RequestParam String session,@RequestParam int studentId,@RequestParam(required=false)Integer secId,@RequestParam(defaultValue="1")String type,@RequestParam(defaultValue="0")String installment,@RequestParam(defaultValue="2")String isNew,@RequestParam(defaultValue="0")String rte,@RequestParam(defaultValue="false")boolean reprint,Authentication a){
        String sc=school(a),ses=session(session,sc);Map<String,Object> result=new LinkedHashMap<>();
        Map<String,Object> student=jdbc.queryForMap("SELECT TOP 1 sm.stu_id AS studentId,sm.registration_no AS admissionNo,sm.FeeBookNo AS feeBookNo,sm.session,sm.sec_id AS secId,sm.class_id AS classId,sm.isnew,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,COALESCE(so.father_name,'') AS fatherName,COALESCE(so.gender,'') AS gender,COALESCE(so.RTE,0) AS rte,COALESCE(cn.class_name,'') AS className,COALESCE(cl.section,'') AS section FROM TB_StudentMaster sm JOIN student_other_info so ON so.school_id=sm.school_id AND so.registration_no=sm.registration_no AND so.uid=sm.uid LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id LEFT JOIN TB_Class cl ON cl.school_id=sm.school_id AND cl.sec_id=sm.sec_id WHERE sm.school_id=? AND sm.session=? AND sm.stu_id=?",sc,ses,studentId);
        result.put("type",type);result.put("reprint",reprint);result.put("session",ses);result.put("student",student);result.put("school",school(sc));
        if("2".equals(type)){result.put("rows",frontRows(student));return result;}
        String inst=text(installment);StringBuilder q=new StringBuilder("WITH I AS (SELECT DISTINCT installment_no,MAX(COALESCE(installment_dis,'')) AS installmentDis,MAX(COALESCE(inst_code,'')) AS instCode FROM Installment_Master WHERE school_id=? AND session=?");List<Object> p=new ArrayList<>(List.of(sc,ses));if(!inst.isBlank()&&!"0".equals(inst)){q.append(" AND installment_no=?");p.add(Integer.parseInt(inst));}q.append(" GROUP BY installment_no), H AS (SELECT h.fee_head_id,h.fee_head,h.fee_type_id,h.isOptional FROM fee_head_master h WHERE h.school_id=? AND h.session=? AND h.status=1 AND h.fee_type_id<>12), C AS (SELECT fc.fee_head_id,fc.fee_type_id,fc.fee_charges FROM fee_head_charge fc WHERE fc.school_id=? AND fc.session=? AND fc.class_id=? AND fc.status=1) SELECT I.installment_no AS installmentNo,I.installmentDis,I.instCode,H.fee_head_id AS feeHeadId,CASE WHEN H.fee_type_id=3 THEN 'ADMISSION FEE' ELSE H.fee_head END AS feeHead,H.fee_type_id AS feeTypeId,COALESCE(C.fee_charges,0) AS headAmount,SUM(COALESCE(C.fee_charges,0)) OVER(PARTITION BY I.installment_no) AS totalFee FROM I JOIN H ON 1=1 LEFT JOIN C ON C.fee_head_id=H.fee_head_id AND C.fee_type_id=H.fee_type_id LEFT JOIN Assign_Optional_Fee AO ON AO.school_id=? AND AO.session=? AND AO.stu_id=? AND AO.fee_head_id=H.fee_head_id AND AO.fee_type_id=H.fee_type_id AND AO.installment_no=I.installment_no AND (AO.Paid_status=0 OR AO.Paid_status IS NULL) WHERE (H.fee_type_id=3 OR AO.id IS NOT NULL) ORDER BY I.installment_no,CASE WHEN H.fee_type_id=3 THEN 1 WHEN H.fee_type_id=8 THEN 2 ELSE 4 END,H.fee_head_id");p.addAll(List.of(sc,ses,sc,ses,student.get("classId"),sc,ses,studentId));List<Map<String,Object>> rows=jdbc.queryForList(q.toString(),p.toArray());addTransportRows(rows,sc,ses,studentId);result.put("rows",rows);return result;
    }

    private Map<String,Object> school(String sc){List<Map<String,Object>> r=jdbc.queryForList("SELECT TOP 1 schoolname,web_address,logo_path,bank_branch,bank_acc,bank_name,tc_add1,tc_add2,tc_add3,tc_add4,phone,mobile,IFSC FROM schoolmaster WHERE school_id=?",sc);return r.isEmpty()?new LinkedHashMap<>():r.get(0);}
    private void addTransportRows(List<Map<String,Object>> rows,String sc,String ses,int studentId){Integer enabled=jdbc.queryForObject("SELECT TOP 1 COALESCE(transport,0) FROM schoolmaster WHERE school_id=?",Integer.class,sc);if(enabled==null||enabled!=1)return;Set<String> installments=new LinkedHashSet<>();for(Map<String,Object> r:rows)installments.add(text(r.get("installmentNo")));for(String no:installments){Map<String,Object> first=rows.stream().filter(r->no.equals(text(r.get("installmentNo")))).findFirst().orElse(null);if(first==null)continue;Map<String,Object> t=new LinkedHashMap<>();t.put("installmentNo",first.get("installmentNo"));t.put("installmentDis",first.get("installmentDis"));t.put("instCode",first.get("instCode"));t.put("feeHeadId",0);t.put("feeHead","TRANSPORT FEE");t.put("feeTypeId",6);t.put("headAmount",0);t.put("totalFee",first.get("totalFee"));rows.add(t);}rows.sort(Comparator.comparing((Map<String,Object> r)->text(r.get("installmentNo"))).thenComparing(r->Integer.parseInt(text(r.getOrDefault("feeTypeId","99")))));}
    private List<Map<String,Object>> frontRows(Map<String,Object> student){return List.of(new LinkedHashMap<>(Map.of("studentName",text(student.get("studentName")),"fatherName",text(student.get("fatherName")),"className",text(student.get("className")),"section",text(student.get("section")),"admissionNo",text(student.get("admissionNo")),"feeBookNo",text(student.get("feeBookNo")),"rte",text(student.get("rte")))));}
}
