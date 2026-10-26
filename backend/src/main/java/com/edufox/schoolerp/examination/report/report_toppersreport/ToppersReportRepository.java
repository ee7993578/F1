package com.edufox.schoolerp.examination.report.report_toppersreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ToppersReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_toppersreport_toppersreportrepository_java")
public class ToppersReportRepository extends PageArtifactRepository {
    public ToppersReportRepository() {
        super("ExamMaster", "report", "ToppersReport", "ExamMaster/Report/ToppersReport.jsx");
    }
}