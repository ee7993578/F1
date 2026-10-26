package com.edufox.schoolerp.examination.report.report_overallexaminationreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/OverallExaminationReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_overallexaminationreport_overallexaminationreportrepository_java")
public class OverallExaminationReportRepository extends PageArtifactRepository {
    public OverallExaminationReportRepository() {
        super("ExamMaster", "report", "OverallExaminationReport", "ExamMaster/Report/OverallExaminationReport.jsx");
    }
}