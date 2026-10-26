package com.edufox.schoolerp.staff.shared.staff;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.edufox.schoolerp.config.DatabaseWriteGuard;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository repository;
    private final DatabaseWriteGuard writeGuard;
    public Map<String,Object> bootstrap(String school,String session,boolean light){return repository.bootstrap(school,session,light);}
    public List<Map<String,Object>> list(String school,String session,String search,boolean withdrawn){return repository.list(school,session,search,withdrawn);}
    public Map<String,Object> find(String school,String id){return repository.find(school,id);}
    public StaffDtos.MutationResponse register(String school,String by,String session,Map<String,Object>b){writeGuard.requireWritable();return repository.register(school,by,session,b);}
    public StaffDtos.MutationResponse update(String school,String id,Map<String,Object>b,String by){writeGuard.requireWritable();return repository.update(school,id,b,by);}
    public StaffDtos.MutationResponse withdraw(String school,String id,String session,String reason,String by){writeGuard.requireWritable();return repository.withdraw(school,id,session,reason,by);}
    public StaffDtos.MutationResponse reregister(String school,String id,String session,String by){writeGuard.requireWritable();return repository.reregister(school,id,session,by);}
    public StaffDtos.MutationResponse credentials(String school,String id,Map<String,Object>b){writeGuard.requireWritable();return repository.credentials(school,id,b);}
    public List<Map<String,Object>> classTeachers(String school,String session,String staffUid){List<Map<String,Object>> rows=repository.classTeacherReport(school,session);if(!repository.canViewCredentialsForReport(school,staffUid))rows.forEach(r->{r.remove("password");r.remove("userId");});return rows;}
    public Map<String,Object> classTeachersView(String school,String session,String staffUid){
        boolean canView=repository.canViewCredentialsForReport(school,staffUid);
        List<Map<String,Object>> rows=repository.classTeacherReport(school,session);
        if(!canView) rows.forEach(r->{r.remove("password");r.remove("userId");});
        return Map.of("rows",rows,"canViewCredentials",canView);
    }
    public Map<String,Object> facultyInfoColumns(){return repository.facultyInfoColumns();}
    public Map<String,Object> facultyInfoReport(String school,String session,String status,String columns,String staffUid){return repository.facultyInfoReport(school,session,status,columns,staffUid);}
    public Map<String,Object> credentialsReport(String school,String session,String type,String staffUid){return repository.credentialsReport(school,session,type,staffUid);}
    public Map<String,Object> classTeacherSetup(String school,String session){return repository.classTeacherSetup(school,session);}
    public StaffDtos.MutationResponse saveClassTeachers(String school,String by,StaffDtos.ClassTeacherRequest r){writeGuard.requireWritable();return repository.saveClassTeachers(school,by,r);}
    public List<Map<String,Object>> jobConfig(String school,String session){return repository.jobConfig(school,session);}
    public Map<String,Object> jobConfigOptions(String school){return repository.jobConfigOptions(school);}
    public Map<String,Object> jobApplicationOptions(String school,String post){return repository.jobApplicationOptions(school,post);}
    public List<Map<String,Object>> jobApplications(String school,String post,String subject,String month,String status){return repository.jobApplications(school,post,subject,month,status);}
    public StaffDtos.MutationResponse saveJobConfig(String school,String by,Map<String,Object> body){writeGuard.requireWritable();return repository.saveJobConfig(school,by,body);}
    public StaffDtos.MutationResponse updateJobConfig(String school,String by,String id,Map<String,Object> body){writeGuard.requireWritable();return repository.updateJobConfig(school,by,id,body);}
}
