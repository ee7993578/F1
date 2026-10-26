package com.edufox.schoolerp.uploads.report.report_showsyllabus;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Report/ShowSyllabus.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_report_report_showsyllabus_showsyllabusrepository_java")
public class ShowSyllabusRepository extends PageArtifactRepository {
    public ShowSyllabusRepository() {
        super("Uploads", "report", "ShowSyllabus", "Uploads/Report/ShowSyllabus.jsx");
    }
}