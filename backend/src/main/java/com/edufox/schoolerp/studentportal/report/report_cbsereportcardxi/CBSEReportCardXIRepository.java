package com.edufox.schoolerp.studentportal.report.report_cbsereportcardxi;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/CBSEReportCardXI.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_cbsereportcardxi_cbsereportcardxirepository_java")
public class CBSEReportCardXIRepository extends PageArtifactRepository {
    public CBSEReportCardXIRepository() {
        super("StundentPages", "report", "CBSEReportCardXI", "StundentPages/Report/CBSEReportCardXI.jsx");
    }
}