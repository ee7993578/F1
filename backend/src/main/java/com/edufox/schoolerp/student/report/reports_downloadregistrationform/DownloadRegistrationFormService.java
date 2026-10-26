package com.edufox.schoolerp.student.report.reports_downloadregistrationform;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/DownloadRegistrationForm.jsx. */
@Service("page_student_report_reports_downloadregistrationform_downloadregistrationformservice_java")
public class DownloadRegistrationFormService extends PageArtifactService<DownloadRegistrationFormRepository> {
    public DownloadRegistrationFormService(DownloadRegistrationFormRepository repository) {
        super(repository);
    }
}