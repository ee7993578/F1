package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentAdmissionService {
    private final StudentAdmissionRepository repository;
    @Transactional
    public StudentAdmissionDtos.AdmissionResponse allot(String schoolId, String uid, StudentAdmissionDtos.AdmissionRequest request) {
        return repository.allot(schoolId, uid, request);
    }
}