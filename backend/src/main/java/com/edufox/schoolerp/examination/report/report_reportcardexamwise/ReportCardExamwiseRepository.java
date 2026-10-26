package com.edufox.schoolerp.examination.report.report_reportcardexamwise;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ReportCardExamwise.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_reportcardexamwise_reportcardexamwiserepository_java")
public class ReportCardExamwiseRepository extends PageArtifactRepository {
    public ReportCardExamwiseRepository() {
        super("ExamMaster", "report", "ReportCardExamwise", "ExamMaster/Report/ReportCardExamwise.jsx");
    }
}