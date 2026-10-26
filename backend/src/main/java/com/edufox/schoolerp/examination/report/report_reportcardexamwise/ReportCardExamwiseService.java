package com.edufox.schoolerp.examination.report.report_reportcardexamwise;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/ReportCardExamwise.jsx. */
@Service("page_examination_report_report_reportcardexamwise_reportcardexamwiseservice_java")
public class ReportCardExamwiseService extends PageArtifactService<ReportCardExamwiseRepository> {
    public ReportCardExamwiseService(ReportCardExamwiseRepository repository) {
        super(repository);
    }
}