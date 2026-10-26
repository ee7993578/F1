package com.edufox.schoolerp.student.work.work_bulkadmissiononlineregistration;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/BulkAdmissionOnlineRegistration.jsx. */
@Service("page_student_work_work_bulkadmissiononlineregistration_bulkadmissiononlineregistrationservice_java")
public class BulkAdmissionOnlineRegistrationService extends PageArtifactService<BulkAdmissionOnlineRegistrationRepository> {
    public BulkAdmissionOnlineRegistrationService(BulkAdmissionOnlineRegistrationRepository repository) {
        super(repository);
    }
}