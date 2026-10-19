package com.edufox.schoolerp.student;

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
