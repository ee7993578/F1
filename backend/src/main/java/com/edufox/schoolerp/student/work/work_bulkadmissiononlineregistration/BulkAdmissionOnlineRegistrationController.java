package com.edufox.schoolerp.student.work.work_bulkadmissiononlineregistration;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/BulkAdmissionOnlineRegistration.jsx. */
@RestController("page_student_work_work_bulkadmissiononlineregistration_bulkadmissiononlineregistrationcontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_bulkadmissiononlineregistration")
public class BulkAdmissionOnlineRegistrationController extends PageArtifactController<BulkAdmissionOnlineRegistrationService> {
    public BulkAdmissionOnlineRegistrationController(BulkAdmissionOnlineRegistrationService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}