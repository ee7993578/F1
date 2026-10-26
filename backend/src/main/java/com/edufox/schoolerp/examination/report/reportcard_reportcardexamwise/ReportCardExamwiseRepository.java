package com.edufox.schoolerp.examination.report.reportcard_reportcardexamwise;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Reportcard/ReportCardExamwise.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reportcard_reportcardexamwise_reportcardexamwiserepository_java")
public class ReportCardExamwiseRepository extends PageArtifactRepository {
    public ReportCardExamwiseRepository() {
        super("ExamMaster", "report", "ReportCardExamwise", "ExamMaster/Reportcard/ReportCardExamwise.jsx");
    }
}