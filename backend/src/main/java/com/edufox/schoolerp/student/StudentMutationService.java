package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentMutationService {
    private final StudentMutationRepository repository;

    @Transactional
    public StudentMutationDtos.MutationResponse update(String schoolId, String uid, StudentMutationDtos.UpdateRequest request) {
        return repository.update(schoolId, uid, request);
    }

    @Transactional
    public StudentMutationDtos.MutationResponse delete(String schoolId, String uid) {
        return repository.softDelete(schoolId, uid);
    }
}