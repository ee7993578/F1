package com.edufox.schoolerp.studentportal.report.report_cbsereportcardxi;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/CBSEReportCardXI.jsx. */
@Service("page_studentportal_report_report_cbsereportcardxi_cbsereportcardxiservice_java")
public class CBSEReportCardXIService extends PageArtifactService<CBSEReportCardXIRepository> {
    public CBSEReportCardXIService(CBSEReportCardXIRepository repository) {
        super(repository);
    }
}