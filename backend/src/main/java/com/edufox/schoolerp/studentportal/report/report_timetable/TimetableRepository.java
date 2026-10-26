package com.edufox.schoolerp.studentportal.report.report_timetable;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/Timetable.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_timetable_timetablerepository_java")
public class TimetableRepository extends PageArtifactRepository {
    public TimetableRepository() {
        super("StundentPages", "report", "Timetable", "StundentPages/Report/Timetable.jsx");
    }
}