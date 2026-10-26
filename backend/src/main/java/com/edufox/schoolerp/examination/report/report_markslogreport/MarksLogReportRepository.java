package com.edufox.schoolerp.examination.report.report_markslogreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/MarksLogReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_markslogreport_markslogreportrepository_java")
public class MarksLogReportRepository extends PageArtifactRepository {
    public MarksLogReportRepository() {
        super("ExamMaster", "report", "MarksLogReport", "ExamMaster/Report/MarksLogReport.jsx");
    }
}