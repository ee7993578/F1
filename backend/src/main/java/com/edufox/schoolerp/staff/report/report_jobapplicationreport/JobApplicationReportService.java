package com.edufox.schoolerp.staff.report.report_jobapplicationreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Report/JobApplicationReport.jsx. */
@Service("page_staff_report_report_jobapplicationreport_jobapplicationreportservice_java")
public class JobApplicationReportService extends PageArtifactService<JobApplicationReportRepository> {
    public JobApplicationReportService(JobApplicationReportRepository repository) {
        super(repository);
    }
}