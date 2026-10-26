package com.edufox.schoolerp.studentportal.report.report_cbscprimaryreportcard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/CBSCPrimaryReportCard.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_cbscprimaryreportcard_cbscprimaryreportcardrepository_java")
public class CBSCPrimaryReportCardRepository extends PageArtifactRepository {
    public CBSCPrimaryReportCardRepository() {
        super("StundentPages", "report", "CBSCPrimaryReportCard", "StundentPages/Report/CBSCPrimaryReportCard.jsx");
    }
}