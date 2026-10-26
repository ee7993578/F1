package com.edufox.schoolerp.student.report.reports_onlineregistrationreportstatus;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/OnlineRegistrationReportStatus.jsx. */
@Service("page_student_report_reports_onlineregistrationreportstatus_onlineregistrationreportstatusservice_java")
public class OnlineRegistrationReportStatusService extends PageArtifactService<OnlineRegistrationReportStatusRepository> {
    public OnlineRegistrationReportStatusService(OnlineRegistrationReportStatusRepository repository) {
        super(repository);
    }
}