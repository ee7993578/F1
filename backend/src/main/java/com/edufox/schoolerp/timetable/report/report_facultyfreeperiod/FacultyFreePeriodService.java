package com.edufox.schoolerp.timetable.report.report_facultyfreeperiod;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Report/FacultyFreePeriod.jsx. */
@Service("page_timetable_report_report_facultyfreeperiod_facultyfreeperiodservice_java")
public class FacultyFreePeriodService extends PageArtifactService<FacultyFreePeriodRepository> {
    public FacultyFreePeriodService(FacultyFreePeriodRepository repository) {
        super(repository);
    }
}