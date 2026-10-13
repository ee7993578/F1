package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationOpenService {
    private final RegistrationOpenRepository repository;
    public List<Map<String,Object>> list(String schoolId,String session){return repository.list(schoolId,session);}
    @Transactional public RegistrationOpenDtos.RegistrationResponse save(String schoolId,String staffUid,RegistrationOpenDtos.RegistrationRequest r){return repository.save(schoolId,staffUid,r);}
}