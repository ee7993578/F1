package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentAdmissionService {
    private final StudentAdmissionRepository repository;
    private final DatabaseWriteGuard writeGuard;
    @Transactional
    public StudentAdmissionDtos.AdmissionResponse allot(String schoolId, String uid, StudentAdmissionDtos.AdmissionRequest request) {
        writeGuard.requireWritable();
        return repository.allot(schoolId, uid, request);
    }
}
