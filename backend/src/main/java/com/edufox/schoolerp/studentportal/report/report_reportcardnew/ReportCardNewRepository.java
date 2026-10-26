package com.edufox.schoolerp.studentportal.report.report_reportcardnew;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/ReportCardNew.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_reportcardnew_reportcardnewrepository_java")
public class ReportCardNewRepository extends PageArtifactRepository {
    public ReportCardNewRepository() {
        super("StundentPages", "report", "ReportCardNew", "StundentPages/Report/ReportCardNew.jsx");
    }
}