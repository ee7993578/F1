package com.edufox.schoolerp.student;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentMutationService {
    private final StudentMutationRepository repository;
    private final DatabaseWriteGuard writeGuard;

    @Transactional
    public StudentMutationDtos.MutationResponse update(String schoolId, String uid, StudentMutationDtos.UpdateRequest request) {
        writeGuard.requireWritable();
        return repository.update(schoolId, uid, request);
    }

    @Transactional
    public StudentMutationDtos.MutationResponse delete(String schoolId, String uid) {
        writeGuard.requireWritable();
        return repository.softDelete(schoolId, uid);
    }

    @Transactional
    public StudentMutationDtos.MutationResponse updateWithdrawnReason(String schoolId, StudentMutationDtos.WithdrawReasonRequest request) {
        writeGuard.requireWritable();
        return repository.updateWithdrawnReason(schoolId, request);
    }
}
