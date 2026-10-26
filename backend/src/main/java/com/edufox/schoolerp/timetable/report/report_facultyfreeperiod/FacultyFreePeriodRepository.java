package com.edufox.schoolerp.timetable.report.report_facultyfreeperiod;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Report/FacultyFreePeriod.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_report_report_facultyfreeperiod_facultyfreeperiodrepository_java")
public class FacultyFreePeriodRepository extends PageArtifactRepository {
    public FacultyFreePeriodRepository() {
        super("TimeTable", "report", "FacultyFreePeriod", "TimeTable/Report/FacultyFreePeriod.jsx");
    }
}