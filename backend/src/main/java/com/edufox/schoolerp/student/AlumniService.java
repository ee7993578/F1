package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlumniService {
    private final AlumniRepository repository;
    public List<Map<String,Object>> list(String schoolId){return repository.list(schoolId);}
    @Transactional public AlumniDtos.AlumniResponse save(String schoolId,AlumniDtos.AlumniRequest r){return repository.save(schoolId,r);}
}