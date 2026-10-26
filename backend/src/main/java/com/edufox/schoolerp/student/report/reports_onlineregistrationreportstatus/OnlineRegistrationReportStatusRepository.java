package com.edufox.schoolerp.student.report.reports_onlineregistrationreportstatus;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/OnlineRegistrationReportStatus.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_onlineregistrationreportstatus_onlineregistrationreportstatusrepository_java")
public class OnlineRegistrationReportStatusRepository extends PageArtifactRepository {
    public OnlineRegistrationReportStatusRepository() {
        super("Student", "report", "OnlineRegistrationReportStatus", "Student/Reports/OnlineRegistrationReportStatus.jsx");
    }
}