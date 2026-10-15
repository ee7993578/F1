package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentWriteService {
    private final StudentWriteRepository repository;
    private final DatabaseWriteGuard writeGuard;

    @Transactional
    public StudentWriteDtos.WriteResponse register(
            String schoolId,
            String staffUid,
            StudentWriteDtos.RegistrationRequest request
    ) {
        writeGuard.requireWritable();
        return repository.register(schoolId, staffUid, request);
    }
}
