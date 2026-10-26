package com.edufox.schoolerp.studentportal.report.report_showsyllabus;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/ShowSyllabus.jsx. */
@Service("page_studentportal_report_report_showsyllabus_showsyllabusservice_java")
public class ShowSyllabusService extends PageArtifactService<ShowSyllabusRepository> {
    public ShowSyllabusService(ShowSyllabusRepository repository) {
        super(repository);
    }
}