package com.edufox.schoolerp.student.report.reports_registrationreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/RegistrationReport.jsx. */
@Service("page_student_report_reports_registrationreport_registrationreportservice_java")
public class RegistrationReportService extends PageArtifactService<RegistrationReportRepository> {
    public RegistrationReportService(RegistrationReportRepository repository) {
        super(repository);
    }
}