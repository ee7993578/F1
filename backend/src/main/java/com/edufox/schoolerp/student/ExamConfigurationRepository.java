package com.edufox.schoolerp.student;

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
          SELECT e.id,e.classId,e.examDate,e.examReportingTime,e.examEntryGateNO,e.examStartTime,e.examEndTime,
                 e.examSession,e.studentType,COALESCE(c.class_name,'') AS className
          FROM entranceExamConfiguration e LEFT JOIN class_name c
            ON TRY_CONVERT(int,e.classId)=c.id AND c.school_id=e.schoolId
          WHERE e.schoolId=? %s ORDER BY e.id DESC
          """.formatted(session==null||session.isBlank()?"":"AND e.examSession=?");
        return session==null||session.isBlank()?jdbc.queryForList(q,schoolId):jdbc.queryForList(q,schoolId,session);
    }
    @Transactional
    public ExamConfigurationDtos.ExamResponse save(String schoolId,String staffUid,ExamConfigurationDtos.ExamRequest r){
        if(text(r.classId()).isBlank()||text(r.examSession()).isBlank()||text(r.examDate()).isBlank()) throw new IllegalArgumentException("Class, session and exam date are required");
        if(r.id()==null){
            jdbc.update("""
              INSERT INTO entranceExamConfiguration
                (classId,examDate,examReportingTime,examEntryGateNO,examStartTime,examEndTime,createdBy,createdDate,schoolId,examSession,studentType)
              VALUES (?,?,?,?,?,?,?,CONVERT(varchar(50),GETDATE(),120),?,?,?)
              """,text(r.classId()),text(r.examDate()),text(r.examReportingTime()),text(r.examEntryGateNO()),text(r.examStartTime()),text(r.examEndTime()),staffUid,schoolId,text(r.examSession()),text(r.studentType()));
            Integer id=jdbc.queryForObject("SELECT TOP 1 id FROM entranceExamConfiguration WHERE schoolId=? ORDER BY id DESC",Integer.class,schoolId);
            return new ExamConfigurationDtos.ExamResponse(id,"Exam configuration saved");
        }
        jdbc.update("""
          UPDATE entranceExamConfiguration SET classId=?,examDate=?,examReportingTime=?,examEntryGateNO=?,
            examStartTime=?,examEndTime=?,modifiedBy=?,modifiedDate=CONVERT(varchar(50),GETDATE(),120),examSession=?,studentType=?
          WHERE id=? AND schoolId=?
          """,text(r.classId()),text(r.examDate()),text(r.examReportingTime()),text(r.examEntryGateNO()),text(r.examStartTime()),text(r.examEndTime()),staffUid,text(r.examSession()),text(r.studentType()),r.id(),schoolId);
        return new ExamConfigurationDtos.ExamResponse(r.id(),"Exam configuration updated");
    }
    @Transactional public ExamConfigurationDtos.ExamResponse delete(String schoolId,Integer id){jdbc.update("DELETE FROM entranceExamConfiguration WHERE id=? AND schoolId=?",id,schoolId);return new ExamConfigurationDtos.ExamResponse(id,"Exam configuration deleted");}
    private static String text(String v){return v==null?"":v.trim();}
}