package com.edufox.schoolerp.examination.report.examination_report;
import com.edufox.schoolerp.examination.setup.exam_setup.*;
import com.edufox.schoolerp.examination.update.delete_marks.*;
import com.edufox.schoolerp.examination.work.examination_work.*;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExaminationReportService {
    private final ExaminationReportRepository repository;
    private final DatabaseWriteGuard writeGuard;

    public Map<String,Object> bootstrap(String school,String staff){return repository.bootstrap(school,staff);}
    public Map<String,Object> options(String school,String session,int section,int classId,String term){return repository.options(school,session,section,classId,term);}
    public Map<String,Object> data(String school,Map<String,Object> request){return repository.data(school,request);}
    @Transactional public Map<String,Object> saveAnnual(String school,String by,String title,String date,MultipartFile file)throws IOException{writeGuard.requireWritable();return repository.saveAnnualReport(school,by,title,date,file);}
    @Transactional public Map<String,Object> deleteAnnual(String school,String id){writeGuard.requireWritable();return repository.deleteAnnualReport(school,id);}
}
