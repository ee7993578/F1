package com.edufox.schoolerp.uploads.report.report_assignmentreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Report/AssignmentReport.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_report_report_assignmentreport_assignmentreportrepository_java")
public class AssignmentReportRepository extends PageArtifactRepository {
    public AssignmentReportRepository() {
        super("Uploads", "report", "AssignmentReport", "Uploads/Report/AssignmentReport.jsx");
    }
}