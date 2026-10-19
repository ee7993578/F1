package com.edufox.schoolerp.examination;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Direct-SQL implementation of the Examination/Work pages.
 * The legacy procedures were used only as a behavioural/schema reference;
 * this repository deliberately does not execute procedures.
 */
@Repository
@RequiredArgsConstructor
public class ExaminationWorkRepository {
    private final JdbcTemplate jdbc;

    public Map<String, Object> bootstrap(String school, String staffUid) {
        String session = defaultSession(school);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("session", session);
        out.put("sessions", safe("SELECT session,status FROM TB_Session WHERE school_id=? ORDER BY id DESC", school));
        out.put("classes", classes(school, session, staffUid));
        out.put("faculties", safe("SELECT F_id AS id,UPPER(REPLACE(COALESCE(First_name,'')+' '+COALESCE(Mid_name,'')+' '+COALESCE(Last_name,''),'  ',' ')) AS name FROM TB_FacultyMaster WHERE school_id=? ORDER BY name", school));
        out.put("terms", terms(school, session, null));
        out.put("examTypes", safe("SELECT DISTINCT exam_type_id AS id,exam_type AS name FROM exam_type WHERE school_id=? ORDER BY exam_type_id", school));
        out.put("subjects", safe("SELECT id AS id,Subject AS name,Subject_code AS code FROM TB_SubjectMaster WHERE school_id=? AND (status IS NULL OR status=1) ORDER BY Subject", school));
        out.put("indicatorCategories", reportcardCategories(school, session));
        out.put("examIndicatorCategories", safe("SELECT id AS id,indicator_category AS name FROM tb_indicatorCAT WHERE school_id=? AND status='active' ORDER BY COALESCE(indicatorCATOrder,id)", school));
        out.put("indicators", safe("SELECT ID AS id,indicator_Name AS name,indicator_cat AS categoryId,indicator_category_id AS categoryId2 FROM tb_indicator WHERE school_id=? AND status='active' ORDER BY COALESCE(IndicatorOrder,ID)", school));
        out.put("reportcardCategories", reportcardCategories(school, session));
        out.put("learnerAttributes", safe("SELECT ATTRIBUTE_ID AS id,ATTRIBUTE_NAME AS name,HasAttributeValue AS hasValues FROM TB_LERNERPROFILE_ATTRIBUTE WHERE SCHOOL_ID=? AND SESSION=? AND STATUS='active' ORDER BY COALESCE(ATTR_ORDER,ATTRIBUTE_ID)", school, session));
        out.put("bloodGroups", safe("SELECT id,blood_group AS name FROM blood_group ORDER BY id", new Object[0]));
        out.put("houses", safe("SELECT House_id AS id,H_Name AS name FROM HouseMaster WHERE school_id=? AND (status IS NULL OR status=1) ORDER BY H_Name", school));
        out.put("remarks", safe("SELECT id,remark FROM tb_remarks WHERE school_id=? ORDER BY id", school));
        out.put("grades", List.of(Map.of("id", "-1", "name", "-select-"), Map.of("id", "A", "name", "A"), Map.of("id", "B", "name", "B"), Map.of("id", "C", "name", "C"), Map.of("id", "D", "name", "D"), Map.of("id", "E", "name", "E")));
        return out;
    }

    public String defaultSession(String school) {
        List<Map<String, Object>> r = safe("SELECT TOP 1 session FROM TB_Session WHERE school_id=? AND status='Default' ORDER BY id DESC", school);
        if (!r.isEmpty()) return text(r.get(0).get("session"));
        r = safe("SELECT TOP 1 session FROM TB_Session WHERE school_id=? ORDER BY id DESC", school);
        return r.isEmpty() ? "" : text(r.get(0).get("session"));
    }

    public List<Map<String, Object>> classes(String school, String session, String staffUid) {
        // Keep the legacy class/section binding shape. Teacher restriction is applied when a mapping exists.
        List<Map<String, Object>> all = safe("""
            SELECT DISTINCT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,
                   cn.class_name AS className,cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            WHERE c.school_id=? ORDER BY c.class_id,c.sec_id
            """, school);
        if (staffUid == null || staffUid.isBlank() || all.isEmpty()) return all;
        List<Map<String, Object>> mapped = safe("""
            SELECT DISTINCT c.sec_id AS sectionId,c.class_id AS classId,c.section AS sectionName,
                   cn.class_name AS className,cn.class_name+' ('+c.section+')' AS label
            FROM TB_Class c JOIN class_name cn ON cn.school_id=c.school_id AND cn.id=c.class_id
            JOIN Tb_ClassTeacher ct ON ct.school_id=c.school_id AND ct.section_id=c.sec_id AND ct.session=?
              AND ct.staff_u_id=?
            WHERE c.school_id=? ORDER BY c.class_id,c.sec_id
            """, session, staffUid, school);
        return mapped.isEmpty() ? all : mapped;
    }

    public List<Map<String, Object>> terms(String school, String session, Integer classId) {
        if (classId == null || classId == 0) {
            return safe("""
                SELECT DISTINCT st.term AS id,COALESCE(t.term_name,CONVERT(varchar(20),st.term)) AS name
                FROM session_term st LEFT JOIN term t ON t.term_id=st.term
                WHERE st.school_id=? AND st.Session=? ORDER BY st.term
                """, school, session);
        }
        return safe("""
            SELECT DISTINCT st.term AS id,COALESCE(t.term_name,CONVERT(varchar(20),st.term)) AS name
            FROM session_term st LEFT JOIN term t ON t.term_id=st.term
            WHERE st.school_id=? AND st.Session=? AND st.class_id=? ORDER BY st.term
            """, school, session, classId);
    }

    public List<Map<String, Object>> learnerValues(String school, String session, String attributeId) {
        return safe("""
            SELECT v.id AS rowId,v.ATTRIBUTE_ID AS attributeId,v.Attribute_Value_Id AS id,
                   v.Attribute_Value AS name
            FROM LERNERPROFILE_ATTRIBUTE_Value v
            WHERE v.school_id=? AND v.session=? AND v.ATTRIBUTE_ID=? AND v.status=1
            ORDER BY v.Attribute_Value
            """, school, session, attributeId);
    }

    public List<Map<String, Object>> reportcardCategories(String school, String session) {
        return safe("SELECT id,CategoryName AS name FROM ReportCardCategory WHERE school_id=? AND session=? AND (status IS NULL OR status=1) ORDER BY COALESCE(CategoryOrder,id)", school, session);
    }
    public List<Map<String, Object>> reportcardSubcategories(String school, String session, String categoryId) {
        return safe("SELECT id,CategoryName AS name,HasSubcategoryValue AS hasValues FROM ReportCardSubCategory WHERE school_id=? AND session=? AND CategoryId=? AND (status IS NULL OR status=1) ORDER BY COALESCE(SubCategoryOrder,id)", school, session, categoryId);
    }
    public List<Map<String, Object>> reportcardValues(String school, String session, String subcategoryId) {
        return safe("SELECT id,subcategoryValue AS name FROM reportcardsubcategoryValue WHERE school_id=? AND session=? AND subcategoryid=? AND (status IS NULL OR status=1) ORDER BY subcategoryValue", school, session, subcategoryId);
    }

    public List<Map<String, Object>> marks(String school, String session, int sectionId, String termId, String examTypeId, String subjectId) {
        return safe("""
            SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
              UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
              COALESCE(CONVERT(varchar(40),m.Marks),'') AS marks,COALESCE(m.Absentism,'') AS absentism,
              COALESCE(CONVERT(varchar(40),m.min_marks),'') AS minMarks,COALESCE(CONVERT(varchar(40),m.max_marks),'') AS maxMarks,
              '' AS weightage
            FROM TB_StudentMaster s
            LEFT JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
            LEFT JOIN marks_entry m ON m.school_id=s.school_id AND m.Stu_id=s.stu_id AND m.Session=s.session
              AND m.sec_id=s.sec_id AND m.Subject_id=? AND m.term_id=? AND m.exam_type_id=?
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered'
            ORDER BY UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' '))
            """, subjectId, termId, examTypeId, school, session, sectionId);
    }

    public void saveMarks(String school, String by, Map<String, Object> body) {
        String session = text(body.get("session")); int sectionId = integer(body.get("sectionId"));
        String term = text(body.get("termId")), exam = text(body.get("examTypeId")), subject = text(body.get("subjectId"));
        int classId = scalarInt("SELECT TOP 1 class_id FROM TB_Class WHERE school_id=? AND sec_id=?", school, sectionId);
        for (Map<String, Object> row : rows(body)) {
            int stu = integer(row.get("studentId")); if (stu <= 0) continue;
            String marks = text(row.get("marks")), absent = text(row.get("absentism"));
            int count = scalarInt("SELECT COUNT(*) FROM marks_entry WHERE school_id=? AND Stu_id=? AND Session=? AND sec_id=? AND Subject_id=? AND term_id=? AND exam_type_id=?", school, stu, session, sectionId, subject, term, exam);
            if (count > 0) jdbc.update("UPDATE marks_entry SET Marks=?,Absentism=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND Stu_id=? AND Session=? AND sec_id=? AND Subject_id=? AND term_id=? AND exam_type_id=?", nullable(marks), absent, by, school, stu, session, sectionId, subject, term, exam);
            else jdbc.update("INSERT INTO marks_entry(Stu_id,sec_id,class_id,Subject_id,exam_type_id,term_id,Marks,min_marks,max_marks,Absentism,created_date,created_by,school_id,Session) VALUES(?,?,?,?,?,?,?, ?,?,?,GETDATE(),?,?,?)", stu, sectionId, classId, subject, exam, term, nullable(marks), nullable(row.get("minMarks")), nullable(row.get("maxMarks")), absent, by, school, session);
        }
    }

    public List<Map<String, Object>> health(String school, String session, int sectionId, String term) {
        return safe("""
            SELECT s.House_id AS houseId,s.stu_id AS studentId,s.Registration_NO AS registrationNo,
              UPPER(REPLACE(COALESCE(so.First_Name,'')+' '+COALESCE(so.Mid_Name,'')+' '+COALESCE(so.Last_Name,''),'  ',' ')) AS studentName,
              COALESCE(h.Height,'') AS height,COALESCE(h.Weight,'') AS weight,COALESCE(so.bloodgroup,0) AS bloodGroup,
              COALESCE(h.Vision,0) AS vision,COALESCE(h.teeth,0) AS teeth,COALESCE(h.Oral_Hygiene,0) AS oralHygiene,
              COALESCE(h.Specificaliment,'') AS specificAilment
            FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
            LEFT JOIN HealthStatus h ON h.school_id=s.school_id AND h.Stu_ID=s.stu_id AND h.session=s.session AND h.term_id=?
            WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered'
            ORDER BY studentName
            """, term, school, session, sectionId);
    }

    public void saveHealth(String school, String by, Map<String, Object> body) {
        String session=text(body.get("session")), term=text(body.get("termId")); int sec=integer(body.get("sectionId"));
        for (Map<String,Object> row: rows(body)) {
            int stu=integer(row.get("studentId")); if(stu<=0)continue;
            jdbc.update("DELETE FROM HealthStatus WHERE school_id=? AND Stu_ID=? AND session=? AND term_id=?", school,stu,session,term);
            jdbc.update("INSERT INTO HealthStatus(Stu_ID,Height,Weight,teeth,Blood_Gp,Vision,Oral_Hygiene,Specificaliment,created_by,modified_date,modified_by,school_id,term_id,session) VALUES(?,?,?,?,?,?,?,?,?,GETDATE(),?,?,?,?)", stu,text(row.get("height")),text(row.get("weight")),text(row.get("teeth")),text(row.get("bloodGroup")),text(row.get("vision")),text(row.get("oralHygiene")),text(row.get("specificAilment")),by,"",by,school,term,session);
            jdbc.update("UPDATE TB_StudentMaster SET House_id=? WHERE school_id=? AND session=? AND stu_id=?", nullable(row.get("houseId")),school,session,stu);
            jdbc.update("UPDATE student_other_info SET bloodgroup=? WHERE school_id=? AND registration_no=(SELECT TOP 1 registration_no FROM TB_StudentMaster WHERE school_id=? AND session=? AND stu_id=?)", nullable(row.get("bloodGroup")),school,school,session,stu);
        }
    }

    public List<Map<String,Object>> indicatorGrades(String school,String session,int sectionId,String term,String indicatorId) {
        return safe("""
          SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
           UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
           COALESCE(g.Grade,'') AS grade,COALESCE(g.remark,'') AS remark
          FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
          LEFT JOIN tb_indicator_Grade g ON g.school_id=s.school_id AND g.session=s.session AND g.stu_id=s.stu_id AND g.indicator_ID=? AND g.Term_id=?
          WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered' ORDER BY studentName
          """, indicatorId,term,school,session,sectionId);
    }
    public void saveIndicatorGrades(String school,String by,Map<String,Object> body) {
        String session=text(body.get("session")),term=text(body.get("termId")),indicator=text(body.get("indicatorId"));
        for(Map<String,Object> r:rows(body)){int stu=integer(r.get("studentId"));if(stu<=0)continue;int n=scalarInt("SELECT COUNT(*) FROM tb_indicator_Grade WHERE school_id=? AND session=? AND stu_id=? AND indicator_ID=? AND Term_id=?",school,session,stu,indicator,term);if(n>0)jdbc.update("UPDATE tb_indicator_Grade SET Grade=?,remark=? WHERE school_id=? AND session=? AND stu_id=? AND indicator_ID=? AND Term_id=?",text(r.get("grade")),text(r.get("remark")),school,session,stu,indicator,term);else jdbc.update("INSERT INTO tb_indicator_Grade(school_id,session,indicator_ID,stu_id,Term_id,Grade,remark) VALUES(?,?,?,?,?,?,?)",school,session,indicator,stu,term,text(r.get("grade")),text(r.get("remark")));}
    }

    public List<Map<String,Object>> learnerProfile(String school,String session,int sectionId,String term,String attributeId) {
        return safe("""
          SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
           UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
           COALESCE(r.ATTRIBUTE_REMARK,'') AS remark,COALESCE(r.Attribute_Value_Id,'') AS attributeValueIds
          FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
          LEFT JOIN TB_LERNERPROFILE_ATTRIBUTE_REMARKS r ON r.SCHOOL_ID=s.school_id AND r.SESSION=s.session AND r.STU_ID=s.stu_id AND r.ATTRIBUTE_ID=? AND r.TERM_ID=?
          WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered' ORDER BY studentName
          """,attributeId,term,school,session,sectionId);
    }
    public void saveLearnerProfile(String school,String by,Map<String,Object> body) {
        String session=text(body.get("session")),term=text(body.get("termId")),attribute=text(body.get("attributeId"));int sec=integer(body.get("sectionId"));
        for(Map<String,Object> r:rows(body)){int stu=integer(r.get("studentId"));if(stu<=0)continue;String remark=text(r.get("remark")),ids=text(r.get("attributeValueIds"));int n=scalarInt("SELECT COUNT(*) FROM TB_LERNERPROFILE_ATTRIBUTE_REMARKS WHERE school_id=? AND session=? AND stu_id=? AND ATTRIBUTE_ID=? AND TERM_ID=?",school,session,stu,attribute,term);if(n>0)jdbc.update("UPDATE TB_LERNERPROFILE_ATTRIBUTE_REMARKS SET ATTRIBUTE_REMARK=?,Attribute_Value_Id=?,SEC_ID=?,CREATED_BY=? WHERE school_id=? AND session=? AND stu_id=? AND ATTRIBUTE_ID=? AND TERM_ID=?",remark,ids,sec,by,school,session,stu,attribute,term);else jdbc.update("INSERT INTO TB_LERNERPROFILE_ATTRIBUTE_REMARKS(stu_id,ATTRIBUTE_ID,ATTRIBUTE_REMARK,Attribute_Value_Id,term_id,session,school_id,SEC_ID,STATUS,CREATED_BY,CREATED_DATE) VALUES(?,?,?,?,?,?,?,?,?,?,GETDATE())",stu,attribute,remark,ids,term,session,school,sec,"active",by);}
    }

    public List<Map<String,Object>> remarks(String school,String session,int sectionId,String term,String remarkType,String examType) {
        return safe("""
          SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
           UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
           COALESCE(r.remark,'') AS remark,COALESCE(r.total_Present,'') AS totalPresent,COALESCE(r.total_Days,'') AS totalDays
          FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
          LEFT JOIN stu_remark r ON r.school_id=s.school_id AND r.session=s.session AND r.stu_id=s.stu_id AND r.term_id=? AND r.remark_type=? AND (r.exam_type_id=? OR ?='')
          WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered' ORDER BY studentName
          """,term,remarkType,examType,examType,school,session,sectionId);
    }
    public void saveRemarks(String school,String by,Map<String,Object> body) {
        String session=text(body.get("session")),term=text(body.get("termId")),type=text(body.get("remarkType")),exam=text(body.get("examTypeId"));int sec=integer(body.get("sectionId"));
        for(Map<String,Object> r:rows(body)){int stu=integer(r.get("studentId"));if(stu<=0)continue;int n=scalarInt("SELECT COUNT(*) FROM stu_remark WHERE school_id=? AND session=? AND stu_id=? AND term_id=? AND remark_type=? AND (exam_type_id=? OR ?='')",school,session,stu,term,type,exam,exam);if(n>0)jdbc.update("UPDATE stu_remark SET remark=?,total_Present=?,total_Days=?,sec_id=?,created_by=?,created_date=GETDATE() WHERE school_id=? AND session=? AND stu_id=? AND term_id=? AND remark_type=? AND (exam_type_id=? OR ?='')",text(r.get("remark")).toUpperCase(),nullable(r.get("totalPresent")),nullable(r.get("totalDays")),sec,by,school,session,stu,term,type,exam,exam);else jdbc.update("INSERT INTO stu_remark(stu_id,sec_id,remark_type,remark,term_id,session,school_id,created_by,created_date,total_Present,total_Days,exam_type_id) VALUES(?,?,?,?,?,?,?, ?,GETDATE(),?,?,?)",stu,sec,type,text(r.get("remark")).toUpperCase(),term,session,school,by,nullable(r.get("totalPresent")),nullable(r.get("totalDays")),nullable(exam));}
    }

    public List<Map<String,Object>> reportcardRemarks(String school,String session,int sectionId,String term,String category,String subcategory) {
        return safe("""
          SELECT s.stu_id AS studentId,s.registration_no AS registrationNo,
           UPPER(REPLACE(COALESCE(so.first_name,'')+' '+COALESCE(so.mid_name,'')+' '+COALESCE(so.last_name,''),'  ',' ')) AS studentName,
           COALESCE(r.Remark,'') AS remark
          FROM TB_StudentMaster s JOIN student_other_info so ON so.school_id=s.school_id AND so.registration_no=s.registration_no
          LEFT JOIN ReportCardSubCategoryRemark r ON r.school_id=s.school_id AND r.session=s.session AND r.stu_id=s.stu_id AND r.CategoryId=? AND r.SubCategoryId=? AND r.Term_id=?
          WHERE s.school_id=? AND s.session=? AND s.sec_id=? AND LOWER(s.status)='registered' ORDER BY studentName
          """,category,subcategory,term,school,session,sectionId);
    }
    public void saveReportcardRemarks(String school,String by,Map<String,Object> body) {
        String session=text(body.get("session")),term=text(body.get("termId")),cat=text(body.get("categoryId")),sub=text(body.get("subcategoryId"));int sec=integer(body.get("sectionId"));
        for(Map<String,Object> r:rows(body)){int stu=integer(r.get("studentId"));if(stu<=0)continue;int n=scalarInt("SELECT COUNT(*) FROM ReportCardSubCategoryRemark WHERE school_id=? AND session=? AND stu_id=? AND CategoryId=? AND SubCategoryId=? AND Term_id=?",school,session,stu,cat,sub,term);if(n>0)jdbc.update("UPDATE ReportCardSubCategoryRemark SET Remark=?,created_by=?,created_date=GETDATE() WHERE school_id=? AND session=? AND stu_id=? AND CategoryId=? AND SubCategoryId=? AND Term_id=?",text(r.get("remark")),by,school,session,stu,cat,sub,term);else jdbc.update("INSERT INTO ReportCardSubCategoryRemark(CategoryId,stu_id,Remark,created_date,created_by,Term_id,session,school_id,SubCategoryId) VALUES(?,?,?,GETDATE(),?,?,?,?,?)",cat,stu,text(r.get("remark")),by,term,session,school,sub);}
    }

    public List<Map<String,Object>> greenSheet(String school,String session,int sectionId) {
        return safe("SELECT Row_Id,STU_ID,stu_NAME,CLASS_ID,sec_id,SUB_ID,sub_NAME,PA1,PA2,PT1,NB1,SE1,HY_TH,HY_PR,PA3,PA4,PT2,NB2,SE2,Y_TH,Y_PR FROM exam_reports WHERE school_id=? AND session=? AND sec_id=? ORDER BY stu_NAME", school,session,sectionId);
    }

    /** Reads the same row-oriented workbook used by Upload-marks without a procedure call. */
    public int uploadMarks(String school, String by, String session, int sectionId, MultipartFile file) throws Exception {
        int count = 0;
        DataFormatter formatter = new DataFormatter();
        try (InputStream in = file.getInputStream(); Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0); Row header = sheet.getRow(0);
            if (header == null) throw new IllegalArgumentException("Excel header row is missing");
            Map<String,Integer> columns = new LinkedHashMap<>();
            for (Cell cell : header) columns.put(formatter.formatCellValue(cell).trim().toLowerCase().replace("_", " "), cell.getColumnIndex());
            for (int i=1;i<=sheet.getLastRowNum();i++) {
                Row row=sheet.getRow(i); if(row==null) continue;
                String registration=cell(row,columns,formatter,"registration no","admission no","registration");
                String stuId=cell(row,columns,formatter,"stu id","student id");
                int student=toInt(stuId);
                if(student<=0 && !registration.isBlank()) student=scalarInt("SELECT TOP 1 stu_id FROM TB_StudentMaster WHERE school_id=? AND session=? AND sec_id=? AND registration_no=?",school,session,sectionId,registration);
                String subject=cell(row,columns,formatter,"subject id","subject");
                String exam=cell(row,columns,formatter,"exam type id","exam type","exam");
                String term=cell(row,columns,formatter,"term id","term");
                String marks=cell(row,columns,formatter,"marks","grade");
                if(student<=0 || subject.isBlank() || exam.isBlank() || term.isBlank()) continue;
                String absent=cell(row,columns,formatter,"absentism","absent");
                int exists=scalarInt("SELECT COUNT(*) FROM marks_entry WHERE school_id=? AND Stu_id=? AND Session=? AND sec_id=? AND Subject_id=? AND term_id=? AND exam_type_id=?",school,student,session,sectionId,subject,term,exam);
                if(exists>0) jdbc.update("UPDATE marks_entry SET Marks=?,Absentism=?,modified_date=GETDATE(),modified_by=? WHERE school_id=? AND Stu_id=? AND Session=? AND sec_id=? AND Subject_id=? AND term_id=? AND exam_type_id=?",nullable(marks),absent,by,school,student,session,sectionId,subject,term,exam);
                else jdbc.update("INSERT INTO marks_entry(Stu_id,sec_id,class_id,Subject_id,exam_type_id,term_id,Marks,Absentism,created_date,created_by,school_id,Session) SELECT ?,sec_id,class_id,?,?,?,?,GETDATE(),?,?,? FROM TB_Class WHERE school_id=? AND sec_id=?",student,subject,exam,term,nullable(marks),absent,by,school,school,sectionId);
                count++;
            }
        }
        return count;
    }

    private static String cell(Row row, Map<String,Integer> columns, DataFormatter formatter, String... names) {
        for(String name:names){Integer index=columns.get(name);if(index!=null){Cell c=row.getCell(index);return c==null?"":formatter.formatCellValue(c).trim();}}
        return "";
    }
    private static int toInt(String value){try{return Integer.parseInt(value.trim());}catch(Exception e){return 0;}}

    private List<Map<String,Object>> safe(String sql,Object... args){try{return jdbc.queryForList(sql,args);}catch(DataAccessException e){return List.of();}}
    private int scalarInt(String sql,Object... args){try{Integer n=jdbc.queryForObject(sql,Integer.class,args);return n==null?0:n;}catch(DataAccessException e){return 0;}}
    private static String text(Object v){return v==null?"":String.valueOf(v);}
    private static int integer(Object v){try{return Integer.parseInt(text(v));}catch(Exception e){return 0;}}
    private static Object nullable(Object v){String s=text(v);return s.isBlank()?null:s;}
    @SuppressWarnings("unchecked") private static List<Map<String,Object>> rows(Map<String,Object> b){Object v=b.get("rows");return v instanceof List<?> l?(List<Map<String,Object>>)(List<?>)l:List.of();}
}
