package com.edufox.schoolerp.examination.report.reportanalytics_toppersreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/ReportAnalytics/ToppersReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reportanalytics_toppersreport_toppersreportrepository_java")
public class ToppersReportRepository extends PageArtifactRepository {
    public ToppersReportRepository() {
        super("ExamMaster", "report", "ToppersReport", "ExamMaster/ReportAnalytics/ToppersReport.jsx");
    }
}