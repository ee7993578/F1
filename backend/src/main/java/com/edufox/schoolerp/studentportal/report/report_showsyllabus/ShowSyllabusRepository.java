package com.edufox.schoolerp.studentportal.report.report_showsyllabus;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/ShowSyllabus.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_showsyllabus_showsyllabusrepository_java")
public class ShowSyllabusRepository extends PageArtifactRepository {
    public ShowSyllabusRepository() {
        super("StundentPages", "report", "ShowSyllabus", "StundentPages/Report/ShowSyllabus.jsx");
    }
}