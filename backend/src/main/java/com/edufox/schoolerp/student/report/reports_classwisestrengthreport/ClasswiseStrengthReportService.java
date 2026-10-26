package com.edufox.schoolerp.student.report.reports_classwisestrengthreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/ClasswiseStrengthReport.jsx. */
@Service("page_student_report_reports_classwisestrengthreport_classwisestrengthreportservice_java")
public class ClasswiseStrengthReportService extends PageArtifactService<ClasswiseStrengthReportRepository> {
    public ClasswiseStrengthReportService(ClasswiseStrengthReportRepository repository) {
        super(repository);
    }
}