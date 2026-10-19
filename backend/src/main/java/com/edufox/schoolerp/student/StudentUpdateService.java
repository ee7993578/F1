package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentUpdateService {
    private final StudentUpdateRepository repository;
    private final DatabaseWriteGuard writeGuard;

    @Transactional
    public StudentUpdateDtos.MoveResponse move(String schoolId, StudentUpdateDtos.MoveRequest request) {
        writeGuard.requireWritable();
        return repository.move(schoolId, request);
    }

    public java.util.List<java.util.Map<String, Object>> schools(String schoolId) {
        return repository.schools(schoolId);
    }

    public java.util.Map<String, Object> options(String schoolId, String requestedSchool) {
        return repository.options(schoolId, requestedSchool);
    }
}
