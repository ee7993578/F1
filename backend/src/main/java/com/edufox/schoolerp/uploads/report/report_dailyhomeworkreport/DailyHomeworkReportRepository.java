package com.edufox.schoolerp.uploads.report.report_dailyhomeworkreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Report/DailyHomeworkReport.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_report_report_dailyhomeworkreport_dailyhomeworkreportrepository_java")
public class DailyHomeworkReportRepository extends PageArtifactRepository {
    public DailyHomeworkReportRepository() {
        super("Uploads", "report", "DailyHomeworkReport", "Uploads/Report/DailyHomeworkReport.jsx");
    }
}