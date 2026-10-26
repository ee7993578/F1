package com.edufox.schoolerp.examination.report.reportcard_reportcardexamwise;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Reportcard/ReportCardExamwise.jsx. */
@Service("page_examination_report_reportcard_reportcardexamwise_reportcardexamwiseservice_java")
public class ReportCardExamwiseService extends PageArtifactService<ReportCardExamwiseRepository> {
    public ReportCardExamwiseService(ReportCardExamwiseRepository repository) {
        super(repository);
    }
}