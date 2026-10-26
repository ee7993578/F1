package com.edufox.schoolerp.examination.report.report_expectedboardaveragereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ExpectedBoardAverageReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_expectedboardaveragereport_expectedboardaveragereportrepository_java")
public class ExpectedBoardAverageReportRepository extends PageArtifactRepository {
    public ExpectedBoardAverageReportRepository() {
        super("ExamMaster", "report", "ExpectedBoardAverageReport", "ExamMaster/Report/ExpectedBoardAverageReport.jsx");
    }
}