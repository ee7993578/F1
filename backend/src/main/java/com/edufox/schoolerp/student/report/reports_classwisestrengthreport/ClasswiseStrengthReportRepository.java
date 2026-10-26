package com.edufox.schoolerp.student.report.reports_classwisestrengthreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/ClasswiseStrengthReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_classwisestrengthreport_classwisestrengthreportrepository_java")
public class ClasswiseStrengthReportRepository extends PageArtifactRepository {
    public ClasswiseStrengthReportRepository() {
        super("Student", "report", "ClasswiseStrengthReport", "Student/Reports/ClasswiseStrengthReport.jsx");
    }
}