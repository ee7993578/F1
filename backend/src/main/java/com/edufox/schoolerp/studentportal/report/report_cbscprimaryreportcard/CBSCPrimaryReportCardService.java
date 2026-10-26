package com.edufox.schoolerp.studentportal.report.report_cbscprimaryreportcard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/CBSCPrimaryReportCard.jsx. */
@Service("page_studentportal_report_report_cbscprimaryreportcard_cbscprimaryreportcardservice_java")
public class CBSCPrimaryReportCardService extends PageArtifactService<CBSCPrimaryReportCardRepository> {
    public CBSCPrimaryReportCardService(CBSCPrimaryReportCardRepository repository) {
        super(repository);
    }
}