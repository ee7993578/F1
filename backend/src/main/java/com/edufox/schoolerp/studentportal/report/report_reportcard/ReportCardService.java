package com.edufox.schoolerp.studentportal.report.report_reportcard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/ReportCard.jsx. */
@Service("page_studentportal_report_report_reportcard_reportcardservice_java")
public class ReportCardService extends PageArtifactService<ReportCardRepository> {
    public ReportCardService(ReportCardRepository repository) {
        super(repository);
    }
}