package com.edufox.schoolerp.student.report.reports_housereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/HouseReport.jsx. */
@Service("page_student_report_reports_housereport_housereportservice_java")
public class HouseReportService extends PageArtifactService<HouseReportRepository> {
    public HouseReportService(HouseReportRepository repository) {
        super(repository);
    }
}