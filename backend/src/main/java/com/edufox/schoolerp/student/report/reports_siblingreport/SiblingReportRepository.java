package com.edufox.schoolerp.student.report.reports_siblingreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/SiblingReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_siblingreport_siblingreportrepository_java")
public class SiblingReportRepository extends PageArtifactRepository {
    public SiblingReportRepository() {
        super("Student", "report", "SiblingReport", "Student/Reports/SiblingReport.jsx");
    }
}