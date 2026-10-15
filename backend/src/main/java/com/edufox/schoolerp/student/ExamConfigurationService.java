package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamConfigurationService {
    private final ExamConfigurationRepository repository;
    private final DatabaseWriteGuard writeGuard;
    public List<Map<String,Object>> list(String schoolId,String session){return repository.list(schoolId,session);}
    @Transactional public ExamConfigurationDtos.ExamResponse save(String schoolId,String staffUid,ExamConfigurationDtos.ExamRequest r){writeGuard.requireWritable();return repository.save(schoolId,staffUid,r);}
    @Transactional public ExamConfigurationDtos.ExamResponse delete(String schoolId,Integer id){writeGuard.requireWritable();return repository.delete(schoolId,id);}
}
