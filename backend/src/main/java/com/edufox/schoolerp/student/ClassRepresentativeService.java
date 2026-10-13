package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClassRepresentativeService {
    private final ClassRepresentativeRepository repository;
    public Map<String,Object> list(String schoolId,String session){return repository.list(schoolId,session);}
    @Transactional public Map<String,Object> save(String schoolId,String staffUid,ClassRepresentativeDtos.SaveRequest r){return repository.save(schoolId,staffUid,r);}
}