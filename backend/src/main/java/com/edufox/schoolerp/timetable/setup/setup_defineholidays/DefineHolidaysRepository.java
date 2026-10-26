package com.edufox.schoolerp.timetable.setup.setup_defineholidays;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Setup/DefineHolidays.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_setup_setup_defineholidays_defineholidaysrepository_java")
public class DefineHolidaysRepository extends PageArtifactRepository {
    public DefineHolidaysRepository() {
        super("TimeTable", "setup", "DefineHolidays", "TimeTable/Setup/DefineHolidays.jsx");
    }
}