package com.edufox.schoolerp.examination.report.greensheet_expectedboardaveragereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Greensheet/ExpectedBoardAverageReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_greensheet_expectedboardaveragereport_expectedboardaveragereportrepository_java")
public class ExpectedBoardAverageReportRepository extends PageArtifactRepository {
    public ExpectedBoardAverageReportRepository() {
        super("ExamMaster", "report", "ExpectedBoardAverageReport", "ExamMaster/Greensheet/ExpectedBoardAverageReport.jsx");
    }
}