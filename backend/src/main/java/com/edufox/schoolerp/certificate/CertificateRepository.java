package com.edufox.schoolerp.certificate;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Direct table implementation of the legacy certificate pages.
 *
 * The ASPX pages use procedures and ReportViewer datasets.  This class keeps
 * the same table names, aliases and business rules while executing parameterized
 * SQL from the Java service layer.  No stored procedure is executed here.
 */
@Repository
@RequiredArgsConstructor
public class CertificateRepository {
    private final JdbcTemplate jdbc;
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static int number(Object value) { try { return Integer.parseInt(text(value)); } catch (Exception e) { return 0; } }
    private String defaultSession(String school) {
        List<Map<String,Object>> rows = jdbc.queryForList("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY CASE WHEN status='Default' THEN 0 ELSE 1 END,id DESC", school);
        return rows.isEmpty() ? "" : text(rows.get(0).get("session"));
    }
    private String requestedSession(String school, Object requested) { return text(requested).isBlank() ? defaultSession(school) : text(requested); }

    public Map<String,Object> bootstrap(String school) {
        String session = defaultSession(school);
        Map<String,Object> out = new LinkedHashMap<>();
        out.put("session", session);
        out.put("sessions", jdbc.queryForList("SELECT id,session AS value,session AS label,status FROM TB_Session WHERE school_id=? ORDER BY session DESC", school));
        out.put("classes", jdbc.queryForList("""
            SELECT c.sec_id AS id,c.sec_id AS sectionId,c.class_id AS classId,c.section,
                   cn.class_name AS className,cn.class_inwords AS classInWords,
                   cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE c.school_id=? ORDER BY c.class_id,c.section
            """, school));
        out.put("admissionClasses", jdbc.queryForList("SELECT id,class_name AS name,class_name AS label,class_inwords AS classInWords FROM class_name WHERE school_id=? ORDER BY COALESCE(class_order,id),id", school));
        if ("shps_sw".equalsIgnoreCase(school)) {
            out.put("promotedClasses", jdbc.queryForList("SELECT promoted_class_id AS id,promoted_class AS name,promoted_class AS label FROM class_name WHERE school_id=? AND NULLIF(promoted_class_id,'') IS NOT NULL GROUP BY promoted_class_id,promoted_class ORDER BY promoted_class_id", school));
        } else {
            out.put("promotedClasses", out.get("admissionClasses"));
        }
        out.put("feeHeads", jdbc.queryForList("""
            SELECT CAST(common_head_id AS varchar(50)) AS value,display_fee_head AS label,display_fee_head AS name
            FROM common_fee_head_master WHERE school_id=? AND session=?
            UNION ALL SELECT '99','Transport Fee','Transport Fee' ORDER BY label
            """, school, session));
        out.put("reportTypes", List.of(Map.of("value","Multiple","label","Multiple"), Map.of("value","Composite","label","Composite")));
        out.put("school", school(school));
        return out;
    }

    private Map<String,Object> school(String school) {
        List<Map<String,Object>> rows = jdbc.queryForList("""
            SELECT TOP 1 school_id,schoolname,address,phone,mobile,[e-mail] AS email,
                   affiliation_no,school_code,logo_path,School_logo,Software_Domain,
                   tc_add1,tc_add2,tc_add3,tc_add4,schoolname_app,School_name_Report,
                   principal_signature,watermark,city_id
            FROM schoolmaster WHERE school_id=?
            """, school);
        return rows.isEmpty() ? new LinkedHashMap<>() : rows.get(0);
    }

    public List<Map<String,Object>> students(String school, String session, int secId, String mode) {
        String status;
        if ("registered".equalsIgnoreCase(mode)) {
            status = "sm.status='registered'";
        } else {
            Map<String,Object> section = jdbc.queryForMap("SELECT class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, secId);
            int classId = number(section.get("class_id"));
            status = classId == 18 || ("shps_sw".equalsIgnoreCase(school) && classId == 16)
                ? "1=1" : "sm.status='Withdrawn'";
        }
        return jdbc.queryForList("""
            SELECT sm.stu_id AS id,sm.stu_id AS studentId,sm.Registration_NO AS admissionNo,
                   sm.FeeBookNo AS feeBookNo,sm.class_id AS classId,sm.sec_id AS secId,
                   UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,
                   UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''))))+' ['+sm.Registration_NO+']' AS name,
                   COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mother_name,'') AS motherName,
                   COALESCE(so.gender,'') AS gender,COALESCE(so.dob,'') AS dob,COALESCE(so.r_address,'') AS address
            FROM student_other_info so JOIN TB_StudentMaster sm
              ON so.uid=sm.uid AND so.Registration_NO=sm.Registration_NO AND so.school_id=sm.school_id
            WHERE sm.session=? AND sm.school_id=? AND sm.sec_id=? AND %s
            ORDER BY so.first_name,so.mid_name,so.last_name
            """.formatted(status), session, school, secId);
    }

    private Map<String,Object> student(String school, String session, int studentId) {
        return jdbc.queryForMap("""
            SELECT TOP 1 sm.stu_id AS studentId,sm.Registration_NO AS registrationNo,
                   sm.class_id AS classId,sm.sec_id AS secId,sm.session,
                   UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS studentName,
                   COALESCE(so.first_name,'') AS firstName,COALESCE(so.mid_name,'') AS midName,COALESCE(so.last_name,'') AS lastName,
                   COALESCE(so.father_name,'') AS fatherName,COALESCE(so.mother_name,'') AS motherName,
                   COALESCE(so.gender,'') AS gender,COALESCE(so.dob,'') AS dob,COALESCE(so.r_address,'') AS address,
                   COALESCE(so.childcat,'0') AS category,COALESCE(so.nationality,'Indian') AS nationality,
                   COALESCE(so.PEN_No,'') AS penNo,COALESCE(so.annual_income,'') AS annualIncome,
                   COALESCE(so.FeeBookNo,sm.FeeBookNo,'') AS feeBookNo,
                   COALESCE(cn.class_name,'') AS className,COALESCE(cn.class_inwords,'') AS classInWords,
                   COALESCE(c.section,'') AS section,COALESCE(sm.admission_date,'') AS admissionDate,
                   COALESCE(sm.withdrawn_date,'') AS withdrawnDate
            FROM TB_StudentMaster sm JOIN student_other_info so ON so.uid=sm.uid AND so.Registration_NO=sm.Registration_NO AND so.school_id=sm.school_id
            LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id
            LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id
            WHERE sm.school_id=? AND sm.session=? AND sm.stu_id=?
            """, school, session, studentId);
    }

    public Map<String,Object> bonafide(String school, String session, int studentId, String issueDate) {
        Map<String,Object> row = jdbc.queryForMap("""
            SELECT TOP 1
              (CASE WHEN st.gender='Male' THEN 'Master' ELSE 'Miss' END) AS salutation,
              LTRIM(RTRIM(COALESCE(st.first_name,'')+' '+COALESCE(st.mid_name,'')+' '+COALESCE(st.last_name,''))) AS name,
              (CASE WHEN st.gender='Male' THEN 'S/O' ELSE 'D/O' END) AS status,
              COALESCE(st.father_name,'') AS father_name,COALESCE(st.mother_name,'') AS mother_name,COALESCE(st.r_address,'') AS R_Address,
              COALESCE(pd.salutation+' '+pd.P_name,'') AS P_name,
              CONVERT(varchar(50),TRY_CONVERT(date,st.DOB),106) AS DOB,
              COALESCE(CONVERT(varchar(50),TRY_CONVERT(date,st.DOB),106),'') AS inwords,
              cn.class_name+' ('+c.section+')' AS class,sm.schoolname,? AS session,
              COALESCE(sm.Software_Domain,'')+COALESCE(sm.logo_path,'') AS logo_path,
              sm.affiliation_no,sm.address,sm.tc_add1,sm.tc_add2,sm.tc_add3,
              ? AS issue_date,s.Registration_NO AS registration_no,s.stu_id AS studentId
            FROM TB_StudentMaster s
            JOIN student_other_info st ON s.Registration_NO=st.Registration_NO AND s.school_id=st.school_id
            JOIN schoolmaster sm ON sm.school_id=s.school_id
            LEFT JOIN TB_Class c ON c.school_id=s.school_id AND c.sec_id=s.sec_id
            LEFT JOIN class_name cn ON cn.school_id=s.school_id AND cn.id=s.class_id
            LEFT JOIN PrincipalDetail pd ON pd.school_id=s.school_id
            WHERE s.school_id=? AND s.session=? AND s.stu_id=?
            """, session, issueDate, school, session, studentId);
        return row;
    }

    @Transactional
    public Map<String,Object> character(String school, String session, int studentId, String issueDate, String admissionYear, String staff) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM character_certificate WHERE school_id=? AND stu_id=? AND session=? AND issue_date=?", Integer.class, school, studentId, session, issueDate);
        if (count == null || count == 0) {
            jdbc.update("INSERT INTO character_certificate(school_id,session,issue_date,stu_id,created_date,created_by,modified_date,modified_by) VALUES(?,?,?,?,GETDATE(),?,?,?)", school, session, issueDate, studentId, staff, "", "");
        }
        Map<String,Object> row = jdbc.queryForMap("""
            SELECT TOP 1 (CASE WHEN st.gender='Male' THEN 'Master' ELSE 'Miss' END) AS salutation,
              LTRIM(RTRIM(COALESCE(st.first_name,'')+' '+COALESCE(st.mid_name,'')+' '+COALESCE(st.last_name,''))) AS name,
              (CASE WHEN st.gender='Male' THEN 'S/O' ELSE 'D/O' END) AS status,
              COALESCE(NULLIF(LTRIM(RTRIM(st.father_name)),''),'N/A') AS father_name,COALESCE(st.mother_name,'') AS mother_name,
              COALESCE(st.r_address,'') AS R_Address,COALESCE(pd.salutation+' '+pd.P_name,'N/A') AS P_name,
              CONVERT(varchar(50),TRY_CONVERT(date,st.DOB),106) AS DOB,? AS session,
              CASE WHEN st.gender='Male' THEN 'He' ELSE 'She' END AS subject1,
              CASE WHEN st.gender='Male' THEN 'him' ELSE 'her' END AS subject2,
              CASE WHEN st.gender='Male' THEN 'his' ELSE 'her' END AS subject3,
              ? AS adm_year,? AS pass_year,sm.tc_add1,sm.tc_add2,cc.issue_date,
              cn.class_name+' ('+c.section+')' AS class,sm.schoolname,
              COALESCE(sm.Software_Domain,'')+COALESCE(sm.logo_path,'') AS logo_path,
              sm.address,sm.school_code AS SchoolCode,sm.affiliation_no AS AffiliationNo,
              cc.id AS certificate_No,'' AS Remark,sm.schoolname AS schoolName,
              s.stu_id AS studentId
            FROM TB_StudentMaster s JOIN student_other_info st ON s.Registration_NO=st.Registration_NO AND s.school_id=st.school_id
            JOIN schoolmaster sm ON sm.school_id=s.school_id JOIN character_certificate cc ON cc.school_id=s.school_id AND cc.stu_id=s.stu_id AND cc.session=s.session
            LEFT JOIN TB_Class c ON c.school_id=s.school_id AND c.sec_id=s.sec_id LEFT JOIN class_name cn ON cn.school_id=s.school_id AND cn.id=s.class_id
            LEFT JOIN PrincipalDetail pd ON pd.school_id=s.school_id
            WHERE s.school_id=? AND s.session=? AND s.stu_id=?
            """, session, admissionYear, String.valueOf(LocalDate.now().getYear()), school, session, studentId);
        return row;
    }

    public List<Map<String,Object>> feeHeads(String school, String session) {
        return jdbc.queryForList("SELECT CAST(common_head_id AS varchar(50)) AS value,display_fee_head AS label FROM common_fee_head_master WHERE school_id=? AND session=? UNION ALL SELECT '99','Transport Fee' ORDER BY label", school, session);
    }

    public List<Map<String,Object>> fee(String school, String session, int secId, int studentId, String selectedHeads, String reportDate, String reportType) {
        String heads = text(selectedHeads).isBlank() ? "0" : selectedHeads;
        List<Map<String,Object>> rows = jdbc.queryForList("""
            WITH R0 AS (
              SELECT fsl.stu_id,
                CASE WHEN ?='Composite' THEN CASE WHEN cfh.display_fee_head LIKE '%Tuition%' THEN 'Tuition Fee' ELSE 'Composite Fee' END ELSE cfh.display_fee_head END AS IndividualHeadName,
                SUM(COALESCE(fsl.head_amount,0)) AS head_wise_amount
              FROM fee_student_ledger fsl
              JOIN fee_head_master fh ON fh.school_id=fsl.school_id AND fh.session=fsl.session AND fh.fee_head_id=fsl.fee_head_id AND fh.fee_type_id=fsl.fee_type_id
              JOIN common_fee_head_master cfh ON cfh.school_id=fh.school_id AND cfh.session=fh.session AND cfh.common_head_id=fh.common_head_id
              WHERE fsl.school_id=? AND fsl.session=? AND fsl.stu_id=?
                AND CHARINDEX(','+CONVERT(varchar(50),fh.common_head_id)+',', ','+?+',') > 0
              GROUP BY fsl.stu_id,cfh.display_fee_head
            ), R AS (
              SELECT stu_id,IndividualHeadName,SUM(head_wise_amount) AS head_wise_amount
              FROM R0 GROUP BY stu_id,IndividualHeadName
            ), D AS (
              SELECT FORMAT(MIN(i.due_date),'dd MMMM yyyy') AS fromdate,FORMAT(MAX(i.due_date),'dd MMMM yyyy') AS todate,
                     FORMAT(MIN(i.due_date),'MMMM yyyy') AS fromdate1,FORMAT(MAX(i.due_date),'MMMM yyyy') AS todate1
              FROM Installment_Master i JOIN fee_head_master fh ON fh.school_id=i.school_id AND fh.session=i.session AND fh.fee_head_id=i.fee_head_id AND fh.fee_type_id=i.fee_type_id
              WHERE i.school_id=? AND i.session=? AND i.status=1
                AND CHARINDEX(','+CONVERT(varchar(50),fh.common_head_id)+',', ','+?+',') > 0
            )
            SELECT D.fromdate,D.todate,D.fromdate1,D.todate1,
              R.IndividualHeadName, R.head_wise_amount,
              CASE WHEN R.IndividualHeadName LIKE '%Tuition%' THEN '('+CONVERT(varchar(50),R.head_wise_amount/12)+' X 12)' ELSE '' END AS permonthfee_charges,
              SUM(R.head_wise_amount) OVER() AS totalfee_charges,
              (CASE WHEN st.gender='Male' THEN 'Mas' ELSE 'Miss' END) AS salutation,
              (CASE WHEN st.gender='Male' THEN 'S/O' ELSE 'D/O' END) AS status,
              st.R_Address,? AS session,s.Registration_NO AS registration_no,
              UPPER(LTRIM(RTRIM(COALESCE(st.first_name,'')+' '+COALESCE(st.mid_name,'')+' '+COALESCE(st.last_name,'')))) AS name,
              UPPER(COALESCE(st.father_name,'')) AS father_name,UPPER(COALESCE(st.mother_name,'')) AS mother_name,
              sm.affiliation_no,sm.schoolname,sm.address,COALESCE(sm.Software_Domain,'')+COALESCE(sm.logo_path,'') AS logo_path,
              CONVERT(varchar(50),TRY_CONVERT(date,st.DOB),106) AS DOB,? AS inwords,
              cn.class_name AS class_name,c.section,? AS dated,s.stu_id
            FROM R CROSS JOIN D JOIN TB_StudentMaster s ON s.stu_id=R.stu_id AND s.school_id=? AND s.session=?
            JOIN student_other_info st ON st.school_id=s.school_id AND st.Registration_NO=s.Registration_NO
            JOIN schoolmaster sm ON sm.school_id=s.school_id LEFT JOIN class_name cn ON cn.school_id=s.school_id AND cn.id=s.class_id
            LEFT JOIN TB_Class c ON c.school_id=s.school_id AND c.sec_id=s.sec_id
            ORDER BY R.IndividualHeadName
            """, reportType, school, session, studentId, heads, school, session, heads, session, "", reportDate, school, session);
        if (Arrays.asList(heads.split(",")).contains("99")) {
            Map<String,Object> s = student(school, session, studentId);
            Double transport = jdbc.query("SELECT COALESCE(SUM(charges),0) FROM fee_Assign_Transport_Mapping WHERE school_id=? AND stu_id=?", ps -> { ps.setString(1, school); ps.setInt(2, studentId); }, rs -> rs.next() ? rs.getDouble(1) : 0d);
            if (transport != null && transport > 0) {
                Map<String,Object> t = new LinkedHashMap<>();
                t.put("IndividualHeadName","Transport Fee"); t.put("head_wise_amount",transport); t.put("totalfee_charges",transport);
                t.put("salutation","Male".equalsIgnoreCase(text(s.get("gender"))) ? "Mas" : "Miss"); t.put("status","Male".equalsIgnoreCase(text(s.get("gender"))) ? "S/O" : "D/O");
                t.put("R_Address",s.get("address")); t.put("session",session); t.put("registration_no",s.get("registrationNo")); t.put("name",s.get("studentName")); t.put("father_name",s.get("fatherName")); t.put("mother_name",s.get("motherName"));
                t.put("schoolname",school(school).get("schoolname")); t.put("class_name",s.get("className")); t.put("section",s.get("section")); t.put("dated",reportDate); t.put("stu_id",studentId); rows.add(t);
            }
        }
        return rows;
    }

    public Map<String,Object> income(String school, String session, int secId, int studentId) {
        Map<String,Object> row = jdbc.queryForMap("""
            SELECT COALESCE(SUM(fsl.head_amount),0) AS Tution_Fee,
              UPPER(LTRIM(RTRIM(COALESCE(st.first_name,'')+' '+COALESCE(st.mid_name,'')+' '+COALESCE(st.last_name,'')))) AS name,
              COALESCE(st.father_name,'') AS Father_name,(CASE WHEN st.gender='Male' THEN 'S/O' ELSE 'D/O' END) AS SoDo,
              (CASE WHEN st.gender='Male' THEN 'Master' ELSE 'Miss' END) AS Salution,
              (CASE WHEN st.gender='Male' THEN 'he' ELSE 'she' END) AS Heshe,
              cn.class_name+'('+c.section+')' AS class,? AS session,
              LEFT(?,4) AS fromsession,RIGHT(?,4) AS tosession,
              COALESCE(pd.P_name,'') AS principle,sm.affiliation_no,s.stu_id AS studentId
            FROM TB_StudentMaster s JOIN student_other_info st ON st.school_id=s.school_id AND st.Registration_NO=s.Registration_NO
            JOIN schoolmaster sm ON sm.school_id=s.school_id LEFT JOIN TB_Class c ON c.school_id=s.school_id AND c.sec_id=s.sec_id
            LEFT JOIN class_name cn ON cn.school_id=s.school_id AND cn.id=s.class_id LEFT JOIN PrincipalDetail pd ON pd.school_id=s.school_id
            LEFT JOIN fee_student_ledger fsl ON fsl.school_id=s.school_id AND fsl.session=s.session AND fsl.stu_id=s.stu_id AND fsl.fee_type_id=8
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND s.stu_id=?
            GROUP BY st.first_name,st.mid_name,st.last_name,st.father_name,st.gender,cn.class_name,c.section,sm.affiliation_no,pd.P_name,s.stu_id
            """, session, session, session, school, session, secId, studentId);
        return row;
    }

    public Map<String,Object> tcDetail(String school, String session, int secId, int studentId) {
        return jdbc.queryForMap("""
            SELECT TOP 1 s.stu_id AS studentId,s.Registration_NO AS admissionNo,
              COALESCE(t.tc_no,'') AS sno,COALESCE(t.PEN_No,st.PEN_No,'') AS PEN_No,
              UPPER(LTRIM(RTRIM(COALESCE(st.first_name,'')+' '+COALESCE(st.mid_name,'')+' '+COALESCE(st.last_name,'')))) AS stu_name,
              COALESCE(t.enrollment_no,'') AS enroll_no,COALESCE(st.mother_name,'') AS mother_name,COALESCE(st.father_name,'') AS father_name,
              COALESCE(t.stu_category,NULLIF(st.childcat,''),'0') AS stu_category,
              COALESCE(t.dob,CONVERT(varchar(50),TRY_CONVERT(date,st.dob),106)) AS dob,
              COALESCE(t.issue_date,'') AS issue_date,COALESCE(t.admission_date,CONVERT(varchar(50),TRY_CONVERT(date,s.admission_date),106)) AS admission_date,
              COALESCE(t.promote_status,'0') AS promote_status,COALESCE(t.promoted_class,'0') AS promoted_class,
              COALESCE(pc.class_name,'') AS promotedclass,COALESCE(t.last_class,cn.class_name+'('+c.section+')') AS last_class,
              COALESCE(t.last_class_word,cn.class_inwords,'') AS last_class_word1,COALESCE(t.last_date_of_att,'') AS last_date_of_att,
              COALESCE(t.nationality,st.nationality,'Indian') AS nationality,COALESCE(t.failed_status,'0') AS failed_status,
              COALESCE(t.last_exam_result,'') AS last_exam_result,COALESCE(t.qualified_status,'0') AS qualified_status,
              COALESCE(t.paid_fee_status,'0') AS paid_fee_status,COALESCE(t.fee_concession,'0') AS fee_concession,
              COALESCE(t.ncc_cadet_status,'0') AS ncc_cadet_status,COALESCE(t.struck_off_date,'') AS struck_off_date,
              COALESCE(t.no_of_meeting,'') AS no_of_meeting,COALESCE(t.total_att,'') AS total_att,
              COALESCE(t.working_days,'') AS totalWorkingDays,COALESCE(t.general_conduct,'Good') AS general_conduct,
              COALESCE(t.reason_for_leaving,'') AS reason_for_leaving,COALESCE(t.remark,'') AS remark,
              COALESCE(t.extra_activity_status,'') AS extra_activity_status,COALESCE(t.extra_activity,'') AS extra_activity,
              COALESCE(t.application_date,'') AS application_date,COALESCE(t.removal_date,s.withdrawn_date,'') AS withdrawn_date,
              COALESCE(t.subjects_studied,'') AS subjects,COALESCE(t.admission_class,'') AS admission_class,
              COALESCE(ac.class_name,'') AS admissionClass,COALESCE(t.NCC_prov,'') AS txtncc,
              COALESCE(st.r_address,'') AS address,COALESCE(st.nationality,'Indian') AS studentNationality,
              COALESCE(t.tc_fee,'') AS tc_fee,? AS session,
              sm.schoolname,sm.address AS schoolAddress,sm.tc_add1,sm.tc_add2,sm.tc_add3,sm.tc_add4,sm.affiliation_no,
              COALESCE(sm.Software_Domain,'')+COALESCE(sm.logo_path,'') AS logo_path,
              COALESCE(sm.Software_Domain,'')+COALESCE(sm.School_logo,'') AS logo_path1
            FROM TB_StudentMaster s JOIN student_other_info st ON st.school_id=s.school_id AND st.Registration_NO=s.Registration_NO
            JOIN schoolmaster sm ON sm.school_id=s.school_id LEFT JOIN transfer_certificate t ON t.school_id=s.school_id AND t.stu_id=s.stu_id
            LEFT JOIN TB_Class c ON c.school_id=s.school_id AND c.sec_id=s.sec_id LEFT JOIN class_name cn ON cn.school_id=s.school_id AND cn.id=s.class_id
            LEFT JOIN class_name pc ON pc.school_id=s.school_id AND TRY_CONVERT(int,pc.id)=TRY_CONVERT(int,t.promoted_class)
            LEFT JOIN class_name ac ON ac.school_id=s.school_id AND TRY_CONVERT(int,ac.id)=TRY_CONVERT(int,t.admission_class)
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND s.stu_id=?
            """, session, school, session, secId, studentId);
    }

    @Transactional
    public Map<String,Object> saveTc(String school, String staff, Map<String,Object> body) {
        int studentId = number(body.get("studentId"));
        String session = text(body.get("session"));
        Map<String,Object> current = tcDetail(school, session, number(body.get("secId")), studentId);
        String[] columns = {"PEN_No","tc_no","stu_category","dob","issue_date","admission_date","promote_status","promoted_class","last_class","last_date_of_att","nationality","failed_status","last_exam_result","qualified_status","paid_fee_status","fee_concession","ncc_cadet_status","struck_off_date","no_of_meeting","general_conduct","reason_for_leaving","remark","session","extra_activity_status","extra_activity","enrollment_no","school_id","created_by","stu_id","total_att","tc_fee","working_days","application_date","subjects_studied","removal_date","admission_class","NCC_prov","last_class_word"};
        Object[] values = {value(body,"PEN_No",current.get("PEN_No")),value(body,"sno",current.get("sno")),value(body,"stu_category",current.get("stu_category")),value(body,"dob",current.get("dob")),value(body,"issue_date",current.get("issue_date")),value(body,"admission_date",current.get("admission_date")),value(body,"promote_status",current.get("promote_status")),value(body,"promoted_class",current.get("promoted_class")),value(body,"last_class",current.get("last_class")),value(body,"last_date_of_att",current.get("last_date_of_att")),value(body,"nationality",current.get("nationality")),value(body,"failed_status",current.get("failed_status")),value(body,"last_exam_result",current.get("last_exam_result")),value(body,"qualified_status",current.get("qualified_status")),value(body,"paid_fee_status",current.get("paid_fee_status")),value(body,"fee_concession",current.get("fee_concession")),value(body,"ncc_cadet_status",current.get("ncc_cadet_status")),value(body,"struck_off_date",current.get("struck_off_date")),value(body,"no_of_meeting",current.get("no_of_meeting")),value(body,"general_conduct",current.get("general_conduct")),value(body,"reason_for_leaving",current.get("reason_for_leaving")),value(body,"remark",current.get("remark")),session,value(body,"extra_activity_status",current.get("extra_activity_status")),value(body,"extra_activity",current.get("extra_activity")),value(body,"enroll_no",current.get("enroll_no")),school,staff,studentId,value(body,"total_att",current.get("total_att")),value(body,"tc_fee",current.get("tc_fee")),value(body,"totalWorkingDays",current.get("totalWorkingDays")),value(body,"application_date",current.get("application_date")),value(body,"subjects",current.get("subjects")),value(body,"withdrawn_date",current.get("withdrawn_date")),value(body,"admission_class",current.get("admission_class")),value(body,"txtncc",current.get("txtncc")),value(body,"last_class_word1",current.get("last_class_word1"))};
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM transfer_certificate WHERE school_id=? AND stu_id=?", Integer.class, school, studentId);
        if (count != null && count > 0) {
            jdbc.update("UPDATE transfer_certificate SET PEN_No=?,tc_no=?,stu_category=?,dob=?,issue_date=?,admission_date=?,promote_status=?,promoted_class=?,last_class=?,last_date_of_att=?,nationality=?,failed_status=?,last_exam_result=?,qualified_status=?,paid_fee_status=?,fee_concession=?,ncc_cadet_status=?,struck_off_date=?,no_of_meeting=?,general_conduct=?,reason_for_leaving=?,remark=?,session=?,extra_activity_status=?,extra_activity=?,enrollment_no=?,modified_date=GETDATE(),modified_by=?,total_att=?,tc_fee=?,working_days=?,application_date=?,subjects_studied=?,removal_date=?,admission_class=?,NCC_prov=?,last_class_word=? WHERE school_id=? AND stu_id=?", values[0],values[1],values[2],values[3],values[4],values[5],values[6],values[7],values[8],values[9],values[10],values[11],values[12],values[13],values[14],values[15],values[16],values[17],values[18],values[19],values[20],values[21],values[22],values[23],values[24],values[25],staff,values[29],values[30],values[31],values[32],values[33],values[34],values[35],values[36],values[37],school,studentId);
        } else {
            Object[] insertValues = new Object[39];
            System.arraycopy(values, 0, insertValues, 0, 29);
            insertValues[29] = staff;
            System.arraycopy(values, 29, insertValues, 30, 9);
            String placeholders = String.join(",", Collections.nCopies(39, "?"));
            jdbc.update("INSERT INTO transfer_certificate(PEN_No,tc_no,stu_category,dob,issue_date,admission_date,promote_status,promoted_class,last_class,last_date_of_att,nationality,failed_status,last_exam_result,qualified_status,paid_fee_status,fee_concession,ncc_cadet_status,struck_off_date,no_of_meeting,general_conduct,reason_for_leaving,remark,session,extra_activity_status,extra_activity,enrollment_no,school_id,created_by,stu_id,modified_by,total_att,tc_fee,working_days,application_date,subjects_studied,removal_date,admission_class,NCC_prov,last_class_word,modified_date) VALUES (" + placeholders + ",GETDATE())", insertValues);
        }
        return tcDetail(school, session, number(body.get("secId")), studentId);
    }

    private Object value(Map<String,Object> body, String key, Object fallback) { Object v=body.get(key); return v==null?fallback:v; }

    public List<Map<String,Object>> report(String school, String type, String session, int secId, int studentId) {
        return switch (type == null ? "" : type.toLowerCase(Locale.ROOT)) {
            case "bonafide" -> jdbc.queryForList("""
                SELECT bc.issue_date,bc.stu_id,sm.Registration_NO AS registration_no,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS student_name,
                  COALESCE(so.father_name,'') AS father_name,COALESCE(so.mother_name,'') AS mother_name,COALESCE(so.r_address,'') AS address,COALESCE(so.dob,'') AS dob,
                  cn.class_name+'('+c.section+')' AS class_name
                FROM bonafied_certificate bc LEFT JOIN TB_StudentMaster sm ON bc.school_id=sm.school_id AND bc.stu_id=sm.stu_id
                LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.Registration_NO=sm.Registration_NO
                LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id
                WHERE bc.school_id=? ORDER BY student_name""", school);
            case "character" -> jdbc.queryForList("""
                SELECT bc.issue_date,bc.session,bc.stu_id,sm.Registration_NO AS registration_no,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS student_name,
                  COALESCE(so.father_name,'') AS father_name,COALESCE(so.mother_name,'') AS mother_name,COALESCE(so.r_address,'') AS address,COALESCE(so.dob,'') AS dob,
                  cn.class_name+'('+c.section+')' AS class_name
                FROM character_certificate bc LEFT JOIN TB_StudentMaster sm ON bc.school_id=sm.school_id AND bc.stu_id=sm.stu_id AND sm.session=bc.session
                LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.Registration_NO=sm.Registration_NO
                LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id
                WHERE bc.school_id=? ORDER BY student_name""", school);
            case "tc-almamater" -> jdbc.queryForList("""
                SELECT sm.stu_id,sm.Registration_NO AS registration_no,UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS stu_name,
                  cn.class_name+'('+c.section+')' AS class_name,sm.registration_date,sm.withdrawn_date,sm.withdrawn_reason,
                  CASE WHEN tc.stu_id IS NULL THEN 'No' ELSE 'Yes' END AS tc_issued_status,
                  CASE WHEN tc.stu_id IS NULL THEN 'pink' ELSE 'white' END AS color
                FROM TB_StudentMaster sm LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.Registration_NO=sm.Registration_NO
                LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id
                LEFT JOIN (SELECT DISTINCT stu_id FROM transfer_certificate WHERE school_id=? AND session=?) tc ON tc.stu_id=sm.stu_id
                WHERE sm.school_id=? AND sm.status='withdrawn' AND sm.session=? ORDER BY stu_name""", school,session,school,session);
            case "tc" -> tcReport(school,session,secId,studentId);
            default -> List.of();
        };
    }

    private List<Map<String,Object>> tcReport(String school, String session, int secId, int studentId) {
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT tc.stu_id,tc.tc_no,tc.issue_date,tc.tc_fee,tc.fee_concession,
              sm.sec_id AS sec_id,sm.Registration_NO AS AdmNo,
              UPPER(LTRIM(RTRIM(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,'')))) AS [Student Name],
              COALESCE(so.father_name,'') AS [Father Name],COALESCE(so.mother_name,'') AS [Mother Name],cn.class_name+'('+c.section+')' AS [Class],
              COALESCE(so.mobile,'') AS [Mobile],sm.Registration_NO AS [Form No]
            FROM transfer_certificate tc JOIN TB_StudentMaster sm ON sm.school_id=tc.school_id AND sm.stu_id=tc.stu_id AND sm.session=?
            LEFT JOIN student_other_info so ON so.school_id=sm.school_id AND so.Registration_NO=sm.Registration_NO
            LEFT JOIN TB_Class c ON c.school_id=sm.school_id AND c.sec_id=sm.sec_id LEFT JOIN class_name cn ON cn.school_id=sm.school_id AND cn.id=sm.class_id
            WHERE tc.school_id=?
            """
        );
        List<Object> args=new ArrayList<>(List.of(session,school));
        if(secId>0){sql.append(" AND sm.sec_id=?");args.add(secId);} if(studentId>0){sql.append(" AND sm.stu_id=?");args.add(studentId);} sql.append(" ORDER BY [Student Name]");
        List<Map<String,Object>> rows=jdbc.queryForList(sql.toString(),args.toArray());
        double total=rows.stream().mapToDouble(r -> {try{return Double.parseDouble(text(r.get("tc_fee")));}catch(Exception e){return 0;}}).sum();
        Map<String,Object> totalRow=new LinkedHashMap<>(); totalRow.put("isTotal",true);totalRow.put("[Student Name]","Total");totalRow.put("Student Name","Total");totalRow.put("tc_fee",total);rows.add(totalRow); return rows;
    }
}
