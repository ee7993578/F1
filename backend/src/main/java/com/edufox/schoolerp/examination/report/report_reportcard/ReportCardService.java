package com.edufox.schoolerp.examination.report.report_reportcard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/ReportCard.jsx. */
@Service("page_examination_report_report_reportcard_reportcardservice_java")
public class ReportCardService extends PageArtifactService<ReportCardRepository> {
    public ReportCardService(ReportCardRepository repository) {
        super(repository);
    }
}