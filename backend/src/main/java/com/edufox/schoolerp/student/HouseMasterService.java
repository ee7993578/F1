package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HouseMasterService {
    private final HouseMasterRepository repository;
    private final DatabaseWriteGuard writeGuard;
    public List<Map<String,Object>> list(String schoolId) { return repository.list(schoolId); }
    @Transactional public HouseMasterDtos.HouseResponse save(String schoolId,String staffUid,HouseMasterDtos.HouseRequest r){writeGuard.requireWritable();return repository.save(schoolId,staffUid,r);}
    @Transactional public HouseMasterDtos.HouseResponse delete(String schoolId,Integer id){writeGuard.requireWritable();return repository.delete(schoolId,id);}
}
