package com.edufox.schoolerp.examination.report.reports_markslogreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Reports/MarksLogReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reports_markslogreport_markslogreportrepository_java")
public class MarksLogReportRepository extends PageArtifactRepository {
    public MarksLogReportRepository() {
        super("ExamMaster", "report", "MarksLogReport", "ExamMaster/Reports/MarksLogReport.jsx");
    }
}