package com.edufox.schoolerp.student.report.reports_onlineregistrationreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/OnlineRegistrationReport.jsx. */
@Service("page_student_report_reports_onlineregistrationreport_onlineregistrationreportservice_java")
public class OnlineRegistrationReportService extends PageArtifactService<OnlineRegistrationReportRepository> {
    public OnlineRegistrationReportService(OnlineRegistrationReportRepository repository) {
        super(repository);
    }
}