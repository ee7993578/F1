package com.edufox.schoolerp.staff.report.report_jobapplicationreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Report/JobApplicationReport.jsx. */
@Repository("page_com_edufox_schoolerp_staff_report_report_jobapplicationreport_jobapplicationreportrepository_java")
public class JobApplicationReportRepository extends PageArtifactRepository {
    public JobApplicationReportRepository() {
        super("Staff", "report", "JobApplicationReport", "Staff/Report/JobApplicationReport.jsx");
    }
}