package com.edufox.schoolerp.examination.report.report_markstatusreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/MarkStatusReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_markstatusreport_markstatusreportrepository_java")
public class MarkStatusReportRepository extends PageArtifactRepository {
    public MarkStatusReportRepository() {
        super("ExamMaster", "report", "MarkStatusReport", "ExamMaster/Report/MarkStatusReport.jsx");
    }
}