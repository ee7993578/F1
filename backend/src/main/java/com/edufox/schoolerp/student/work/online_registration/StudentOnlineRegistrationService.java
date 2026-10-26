package com.edufox.schoolerp.student.work.online_registration;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentOnlineRegistrationService {
    private final StudentOnlineRegistrationRepository repository;

    public Map<String, Object> detail(String schoolId, long id) { return repository.detail(schoolId, id); }
    public Map<String, Object> confirm(String schoolId, String staffUid, StudentOnlineRegistrationDtos.ConfirmRequest request) {
        return repository.confirm(schoolId, staffUid, request);
    }
    public Map<String, Object> updateStatus(String schoolId, StudentOnlineRegistrationDtos.StatusRequest request) {
        return repository.updateStatus(schoolId, request);
    }
    public Map<String, Object> updatePayment(String schoolId, StudentOnlineRegistrationDtos.PaymentRequest request) {
        return repository.updatePayment(schoolId, request);
    }
}
