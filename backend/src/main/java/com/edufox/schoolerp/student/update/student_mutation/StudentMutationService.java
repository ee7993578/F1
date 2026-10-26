package com.edufox.schoolerp.student.update.student_mutation;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

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
