package com.edufox.schoolerp.examination.report.reportcard_reportcard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Reportcard/ReportCard.jsx. */
@Service("page_examination_report_reportcard_reportcard_reportcardservice_java")
public class ReportCardService extends PageArtifactService<ReportCardRepository> {
    public ReportCardService(ReportCardRepository repository) {
        super(repository);
    }
}