package com.edufox.schoolerp.student.work.work_bulkadmissiononlineregistration;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/BulkAdmissionOnlineRegistration.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_bulkadmissiononlineregistration_bulkadmissiononlineregistrationrepository_java")
public class BulkAdmissionOnlineRegistrationRepository extends PageArtifactRepository {
    public BulkAdmissionOnlineRegistrationRepository() {
        super("Student", "work", "BulkAdmissionOnlineRegistration", "Student/Work/BulkAdmissionOnlineRegistration.jsx");
    }
}