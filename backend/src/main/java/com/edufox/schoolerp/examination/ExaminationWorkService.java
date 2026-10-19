package com.edufox.schoolerp.examination;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExaminationWorkService {
    private final ExaminationWorkRepository repository;
    private final DatabaseWriteGuard writeGuard;
    public Map<String,Object> bootstrap(String school,String staff){return repository.bootstrap(school,staff);}
    public List<Map<String,Object>> terms(String school,String session,Integer classId){return repository.terms(school,session,classId);}
    public List<Map<String,Object>> learnerValues(String school,String session,String id){return repository.learnerValues(school,session,id);}
    public List<Map<String,Object>> reportcardSubcategories(String school,String session,String id){return repository.reportcardSubcategories(school,session,id);}
    public List<Map<String,Object>> reportcardValues(String school,String session,String id){return repository.reportcardValues(school,session,id);}
    public List<Map<String,Object>> marks(String school,String session,int sec,String term,String exam,String subject){return repository.marks(school,session,sec,term,exam,subject);}
    public List<Map<String,Object>> health(String school,String session,int sec,String term){return repository.health(school,session,sec,term);}
    public List<Map<String,Object>> indicatorGrades(String school,String session,int sec,String term,String indicator){return repository.indicatorGrades(school,session,sec,term,indicator);}
    public List<Map<String,Object>> learnerProfile(String school,String session,int sec,String term,String attribute){return repository.learnerProfile(school,session,sec,term,attribute);}
    public List<Map<String,Object>> remarks(String school,String session,int sec,String term,String type,String exam){return repository.remarks(school,session,sec,term,type,exam);}
    public List<Map<String,Object>> reportcardRemarks(String school,String session,int sec,String term,String category,String sub){return repository.reportcardRemarks(school,session,sec,term,category,sub);}
    public List<Map<String,Object>> greenSheet(String school,String session,int sec){return repository.greenSheet(school,session,sec);}
    @Transactional public int uploadMarks(String school,String by,String session,int sec,MultipartFile file)throws Exception{writeGuard.requireWritable();return repository.uploadMarks(school,by,session,sec,file);}
    @Transactional public void saveMarks(String school,String by,Map<String,Object>b){writeGuard.requireWritable();repository.saveMarks(school,by,b);}
    @Transactional public void saveHealth(String school,String by,Map<String,Object>b){writeGuard.requireWritable();repository.saveHealth(school,by,b);}
    @Transactional public void saveIndicatorGrades(String school,String by,Map<String,Object>b){writeGuard.requireWritable();repository.saveIndicatorGrades(school,by,b);}
    @Transactional public void saveLearnerProfile(String school,String by,Map<String,Object>b){writeGuard.requireWritable();repository.saveLearnerProfile(school,by,b);}
    @Transactional public void saveRemarks(String school,String by,Map<String,Object>b){writeGuard.requireWritable();repository.saveRemarks(school,by,b);}
    @Transactional public void saveReportcardRemarks(String school,String by,Map<String,Object>b){writeGuard.requireWritable();repository.saveReportcardRemarks(school,by,b);}
}
