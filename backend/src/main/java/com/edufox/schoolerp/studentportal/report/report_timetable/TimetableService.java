package com.edufox.schoolerp.studentportal.report.report_timetable;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/Timetable.jsx. */
@Service("page_studentportal_report_report_timetable_timetableservice_java")
public class TimetableService extends PageArtifactService<TimetableRepository> {
    public TimetableService(TimetableRepository repository) {
        super(repository);
    }
}