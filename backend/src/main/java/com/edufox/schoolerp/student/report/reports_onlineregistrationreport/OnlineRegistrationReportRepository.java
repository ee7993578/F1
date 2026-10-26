package com.edufox.schoolerp.student.report.reports_onlineregistrationreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/OnlineRegistrationReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_onlineregistrationreport_onlineregistrationreportrepository_java")
public class OnlineRegistrationReportRepository extends PageArtifactRepository {
    public OnlineRegistrationReportRepository() {
        super("Student", "report", "OnlineRegistrationReport", "Student/Reports/OnlineRegistrationReport.jsx");
    }
}