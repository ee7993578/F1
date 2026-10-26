package com.edufox.schoolerp.student.setup.setup_examconfiguration;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Setup/ExamConfiguration.jsx. */
@Service("page_student_setup_setup_examconfiguration_examconfigurationservice_java")
public class ExamConfigurationService extends PageArtifactService<ExamConfigurationRepository> {
    public ExamConfigurationService(ExamConfigurationRepository repository) {
        super(repository);
    }
}