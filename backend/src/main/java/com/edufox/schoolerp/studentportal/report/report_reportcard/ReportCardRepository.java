package com.edufox.schoolerp.studentportal.report.report_reportcard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/ReportCard.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_reportcard_reportcardrepository_java")
public class ReportCardRepository extends PageArtifactRepository {
    public ReportCardRepository() {
        super("StundentPages", "report", "ReportCard", "StundentPages/Report/ReportCard.jsx");
    }
}