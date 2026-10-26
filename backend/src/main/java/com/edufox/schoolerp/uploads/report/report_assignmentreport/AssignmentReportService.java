package com.edufox.schoolerp.uploads.report.report_assignmentreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Report/AssignmentReport.jsx. */
@Service("page_uploads_report_report_assignmentreport_assignmentreportservice_java")
public class AssignmentReportService extends PageArtifactService<AssignmentReportRepository> {
    public AssignmentReportService(AssignmentReportRepository repository) {
        super(repository);
    }
}