package com.edufox.schoolerp.uploads.report.report_academicplannerreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Report/AcademicPlannerReport.jsx. */
@Service("page_uploads_report_report_academicplannerreport_academicplannerreportservice_java")
public class AcademicPlannerReportService extends PageArtifactService<AcademicPlannerReportRepository> {
    public AcademicPlannerReportService(AcademicPlannerReportRepository repository) {
        super(repository);
    }
}