package com.edufox.schoolerp.attendance.work.student_attendance;
import com.edufox.schoolerp.attendance.report.attendance.*;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository repository;
    private final AttendanceReportRepository reportRepository;
    private final DatabaseWriteGuard writeGuard;

    public Map<String,Object> bootstrap(String schoolId,String staffUid){return repository.bootstrap(schoolId,staffUid);}
    public List<Map<String,Object>> dayRows(String schoolId,String session,int sectionId,String date){return repository.dayRows(schoolId,session,sectionId,date);}
    @Transactional public void saveDay(String schoolId,String staffUid,String session,int sectionId,String date,List<Map<String,Object>> rows){writeGuard.requireWritable();repository.saveDay(schoolId,staffUid,session,sectionId,date,rows);}
    public Map<String,Object> monthOptions(String schoolId,String session,int sectionId,Integer term){return repository.monthOptions(schoolId,session,sectionId,term);}
    public Map<String,Object> monthlyRows(String schoolId,String session,int sectionId,int term,int month,int year){return repository.monthlyRows(schoolId,session,sectionId,term,month,year);}
    @Transactional public void saveMonthly(String schoolId,String staffUid,String session,int sectionId,int term,int month,int year,List<Map<String,Object>> rows){writeGuard.requireWritable();repository.saveMonthly(schoolId,staffUid,session,sectionId,term,month,year,rows);}
    public List<Map<String,Object>> deleteRows(String schoolId,String session,int sectionId,String admissionNo,String from,String to){return repository.deleteRows(schoolId,session,sectionId,admissionNo,from,to);}
    @Transactional public int deleteRows(String schoolId,String session,int sectionId,String admissionNo,String from,String to,String staffUid){writeGuard.requireWritable();return repository.deleteRows(schoolId,session,sectionId,admissionNo,from,to,staffUid);}
    @Transactional public int saveHoliday(String schoolId,String session,List<Integer> classIds,String date,String staffUid){writeGuard.requireWritable();return repository.saveHoliday(schoolId,session,classIds,date,staffUid);}
    public List<Map<String,Object>> rfidCampusCount(String schoolId,String session,String date,int sectionId){return repository.rfidCampusCount(schoolId,session,date,sectionId);}
    public List<Map<String,Object>> rfidCampusStudent(String schoolId,String session,String date,int sectionId){return repository.rfidCampusStudent(schoolId,session,date,sectionId);}
    public List<Map<String,Object>> rfidTransportCount(String schoolId,String session,String date){return repository.rfidTransportCount(schoolId,session,date);}
    public List<Map<String,Object>> rfidTransportStudent(String schoolId,String session,String date,int routeId){return repository.rfidTransportStudent(schoolId,session,date,routeId);}
    public Map<String,Object> rfidMatrix(String schoolId,String session,int sectionId,int year,int month,String date){return repository.rfidMatrix(schoolId,session,sectionId,year,month,date);}
    public Map<String,Object> reportBootstrap(String schoolId,String staffUid,String session){return reportRepository.bootstrap(schoolId,staffUid,session);}
    public Map<String,Object> percentageReport(String schoolId,String staffUid,String session,List<Integer> classIds,List<Integer> sectionIds,String fromPer,String toPer,String fromDate,String toDate){return reportRepository.percentageReport(schoolId,staffUid,session,classIds,sectionIds,fromPer,toPer,fromDate,toDate);}
    public Map<String,Object> statusReport(String schoolId,String staffUid,String session,int sectionId,String date){return reportRepository.statusReport(schoolId,staffUid,session,sectionId,date);}
    public Map<String,Object> totalReport(String schoolId,String staffUid,String session,int sectionId){return reportRepository.totalReport(schoolId,staffUid,session,sectionId);}
}
