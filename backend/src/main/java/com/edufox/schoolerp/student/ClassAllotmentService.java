package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassAllotmentService {
    private final ClassAllotmentRepository repository;
    private final DatabaseWriteGuard writeGuard;

    @Transactional
    public ClassAllotmentDtos.ClassAllotmentResponse allot(String schoolId, ClassAllotmentDtos.ClassAllotmentRequest request) {
        writeGuard.requireWritable();
        return repository.allot(schoolId, request);
    }
}
