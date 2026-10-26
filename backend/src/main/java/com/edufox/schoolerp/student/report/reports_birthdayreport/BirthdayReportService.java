package com.edufox.schoolerp.student.report.reports_birthdayreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/BirthdayReport.jsx. */
@Service("page_student_report_reports_birthdayreport_birthdayreportservice_java")
public class BirthdayReportService extends PageArtifactService<BirthdayReportRepository> {
    public BirthdayReportService(BirthdayReportRepository repository) {
        super(repository);
    }
}