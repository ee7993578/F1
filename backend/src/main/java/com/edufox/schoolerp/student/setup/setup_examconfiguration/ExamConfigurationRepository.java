package com.edufox.schoolerp.student.setup.setup_examconfiguration;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Setup/ExamConfiguration.jsx. */
@Repository("page_com_edufox_schoolerp_student_setup_setup_examconfiguration_examconfigurationrepository_java")
public class ExamConfigurationRepository extends PageArtifactRepository {
    public ExamConfigurationRepository() {
        super("Student", "setup", "ExamConfiguration", "Student/Setup/ExamConfiguration.jsx");
    }
}