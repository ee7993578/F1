package com.edufox.schoolerp.student.report.reports_studenttypereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StudentTypeReport.jsx. */
@Service("page_student_report_reports_studenttypereport_studenttypereportservice_java")
public class StudentTypeReportService extends PageArtifactService<StudentTypeReportRepository> {
    public StudentTypeReportService(StudentTypeReportRepository repository) {
        super(repository);
    }
}