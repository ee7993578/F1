package com.edufox.schoolerp.student.report.reports_birthdayreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/BirthdayReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_birthdayreport_birthdayreportrepository_java")
public class BirthdayReportRepository extends PageArtifactRepository {
    public BirthdayReportRepository() {
        super("Student", "report", "BirthdayReport", "Student/Reports/BirthdayReport.jsx");
    }
}