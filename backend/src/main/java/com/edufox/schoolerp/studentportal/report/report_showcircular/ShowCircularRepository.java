package com.edufox.schoolerp.studentportal.report.report_showcircular;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/ShowCircular.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_showcircular_showcircularrepository_java")
public class ShowCircularRepository extends PageArtifactRepository {
    public ShowCircularRepository() {
        super("StundentPages", "report", "ShowCircular", "StundentPages/Report/ShowCircular.jsx");
    }
}