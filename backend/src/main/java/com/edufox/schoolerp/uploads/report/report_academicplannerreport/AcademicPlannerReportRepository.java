package com.edufox.schoolerp.uploads.report.report_academicplannerreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Report/AcademicPlannerReport.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_report_report_academicplannerreport_academicplannerreportrepository_java")
public class AcademicPlannerReportRepository extends PageArtifactRepository {
    public AcademicPlannerReportRepository() {
        super("Uploads", "report", "AcademicPlannerReport", "Uploads/Report/AcademicPlannerReport.jsx");
    }
}