package com.edufox.schoolerp.student.report.reports_registrationreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/RegistrationReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_registrationreport_registrationreportrepository_java")
public class RegistrationReportRepository extends PageArtifactRepository {
    public RegistrationReportRepository() {
        super("Student", "report", "RegistrationReport", "Student/Reports/RegistrationReport.jsx");
    }
}