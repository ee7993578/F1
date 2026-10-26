package com.edufox.schoolerp.student.setup.exam_configuration;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
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
public class ExamConfigurationRepository {
    private final JdbcTemplate jdbc;
    public List<Map<String,Object>> list(String schoolId,String session){
        String q="""
          SELECT e.id,e.classId,CONVERT(varchar(10),TRY_CONVERT(date,e.examDate),23) AS examDate,e.examReportingTime,e.examEntryGateNO,e.examStartTime,e.examEndTime,
                 e.examSession,e.studentType,COALESCE(c.class_name,'') AS className
          FROM entranceExamConfiguration e LEFT JOIN class_name c
            ON TRY_CONVERT(int,e.classId)=c.id AND c.school_id=e.schoolId
          WHERE e.schoolId=? %s ORDER BY TRY_CONVERT(int,e.classId) ASC
          """.formatted(session==null||session.isBlank()?"":"AND e.examSession=?");
        return session==null||session.isBlank()?jdbc.queryForList(q,schoolId):jdbc.queryForList(q,schoolId,session);
    }
    public Map<String,Object> options() {
        return Map.of(
            "hours", jdbc.queryForList("SELECT RIGHT('0'+CONVERT(varchar(2),hour),2) AS value FROM hourtable ORDER BY id"),
            "minutes", jdbc.queryForList("SELECT RIGHT('0'+CONVERT(varchar(2),minutes),2) AS value FROM minutesTable ORDER BY minutes")
        );
    }
    @Transactional
    public ExamConfigurationDtos.ExamResponse save(String schoolId,String staffUid,ExamConfigurationDtos.ExamRequest r){
        if(text(r.classId()).isBlank()||text(r.examSession()).isBlank()||text(r.examDate()).isBlank()) throw new IllegalArgumentException("Class, session and exam date are required");
        String classId=text(r.classId()), session=text(r.examSession()), studentType=text(r.studentType());
        Integer existing = classId.equals("17")
            ? jdbc.query("SELECT TOP 1 id FROM entranceExamConfiguration WHERE schoolId=? AND examSession=? AND classId=? AND ISNULL(studentType,'')=?", rs -> rs.next() ? rs.getInt(1) : null, schoolId,session,classId,studentType)
            : jdbc.query("SELECT TOP 1 id FROM entranceExamConfiguration WHERE schoolId=? AND examSession=? AND classId=?", rs -> rs.next() ? rs.getInt(1) : null, schoolId,session,classId);
        if (existing == null) {
            jdbc.update("""
              INSERT INTO entranceExamConfiguration
                (classId,examDate,examReportingTime,examEntryGateNO,examStartTime,examEndTime,createdBy,createdDate,schoolId,examSession,studentType)
              VALUES (?,TRY_CONVERT(date,?,23),?,?,?,?,?,GETDATE(),?,?,?)
              """,classId,text(r.examDate()),text(r.examReportingTime()),text(r.examEntryGateNO()),text(r.examStartTime()),text(r.examEndTime()),staffUid,schoolId,session,studentType);
            existing = jdbc.queryForObject("SELECT TOP 1 id FROM entranceExamConfiguration WHERE schoolId=? AND examSession=? AND classId=? ORDER BY id DESC",Integer.class,schoolId,session,classId);
            return new ExamConfigurationDtos.ExamResponse(existing,"Exam configuration saved");
        }
        if (classId.equals("17")) {
            jdbc.update("""
              INSERT INTO entranceExamConfigurationLog(classId,examDate,examReportingTime,examEntryGateNO,examStartTime,examEndTime,createdBy,createdDate,schoolId,examSession,studentType,logDate,logBy)
              SELECT classId,examDate,examReportingTime,examEntryGateNO,examStartTime,examEndTime,createdBy,createdDate,schoolId,examSession,studentType,GETDATE(),? FROM entranceExamConfiguration
              WHERE schoolId=? AND examSession=? AND classId=? AND ISNULL(studentType,'')=?
              """,staffUid,schoolId,session,classId,studentType);
            jdbc.update("""
              UPDATE entranceExamConfiguration SET examDate=TRY_CONVERT(date,?),examReportingTime=?,examEntryGateNO=?,examStartTime=?,examEndTime=?,modifiedBy=?,modifiedDate=GETDATE(),studentType=?
              WHERE schoolId=? AND examSession=? AND classId=? AND ISNULL(studentType,'')=?
              """,text(r.examDate()),text(r.examReportingTime()),text(r.examEntryGateNO()),text(r.examStartTime()),text(r.examEndTime()),staffUid,studentType,schoolId,session,classId,studentType);
        } else {
            jdbc.update("""
              INSERT INTO entranceExamConfigurationLog(classId,examDate,examReportingTime,examEntryGateNO,examStartTime,examEndTime,createdBy,createdDate,schoolId,examSession,studentType,logDate,logBy)
              SELECT classId,examDate,examReportingTime,examEntryGateNO,examStartTime,examEndTime,createdBy,createdDate,schoolId,examSession,studentType,GETDATE(),? FROM entranceExamConfiguration
              WHERE schoolId=? AND examSession=? AND classId=?
              """,staffUid,schoolId,session,classId);
            jdbc.update("""
              UPDATE entranceExamConfiguration SET examDate=TRY_CONVERT(date,?),examReportingTime=?,examEntryGateNO=?,examStartTime=?,examEndTime=?,modifiedBy=?,modifiedDate=GETDATE(),studentType=?
              WHERE schoolId=? AND examSession=? AND classId=?
              """,text(r.examDate()),text(r.examReportingTime()),text(r.examEntryGateNO()),text(r.examStartTime()),text(r.examEndTime()),staffUid,studentType,schoolId,session,classId);
        }
        return new ExamConfigurationDtos.ExamResponse(existing,"Exam configuration updated");
    }
    @Transactional public ExamConfigurationDtos.ExamResponse delete(String schoolId,Integer id){
        Map<String,Object> row=jdbc.queryForMap("SELECT classId,examSession,studentType FROM entranceExamConfiguration WHERE id=? AND schoolId=?",id,schoolId);
        String classId=text(String.valueOf(row.get("classId"))), session=text(String.valueOf(row.get("examSession"))), studentType=text(String.valueOf(row.get("studentType")));
        jdbc.update("DELETE FROM entranceExamConfiguration WHERE schoolId=? AND examSession=? AND classId=? " + (classId.equals("17")?"AND ISNULL(studentType,'')=?":""), classId.equals("17")?new Object[]{schoolId,session,classId,studentType}:new Object[]{schoolId,session,classId});
        return new ExamConfigurationDtos.ExamResponse(id,"Exam configuration deleted");
    }
    private static String text(String v){return v==null?"":v.trim();}
}
