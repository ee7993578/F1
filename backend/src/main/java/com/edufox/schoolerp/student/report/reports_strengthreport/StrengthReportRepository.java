package com.edufox.schoolerp.student.report.reports_strengthreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StrengthReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_strengthreport_strengthreportrepository_java")
public class StrengthReportRepository extends PageArtifactRepository {
    public StrengthReportRepository() {
        super("Student", "report", "StrengthReport", "Student/Reports/StrengthReport.jsx");
    }
}