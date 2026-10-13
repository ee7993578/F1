package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentWriteService {
    private final StudentWriteRepository repository;

    @Transactional
    public StudentWriteDtos.WriteResponse register(
            String schoolId,
            String staffUid,
            StudentWriteDtos.RegistrationRequest request
    ) {
        return repository.register(schoolId, staffUid, request);
    }
}