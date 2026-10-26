package com.edufox.schoolerp.student.report.reports_studentwithdrawnreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StudentWithdrawnReport.jsx. */
@Service("page_student_report_reports_studentwithdrawnreport_studentwithdrawnreportservice_java")
public class StudentWithdrawnReportService extends PageArtifactService<StudentWithdrawnReportRepository> {
    public StudentWithdrawnReportService(StudentWithdrawnReportRepository repository) {
        super(repository);
    }
}