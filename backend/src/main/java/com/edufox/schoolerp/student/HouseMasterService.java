package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HouseMasterService {
    private final HouseMasterRepository repository;
    public List<Map<String,Object>> list(String schoolId) { return repository.list(schoolId); }
    @Transactional public HouseMasterDtos.HouseResponse save(String schoolId,String staffUid,HouseMasterDtos.HouseRequest r){return repository.save(schoolId,staffUid,r);}
    @Transactional public HouseMasterDtos.HouseResponse delete(String schoolId,Integer id){return repository.delete(schoolId,id);}
}