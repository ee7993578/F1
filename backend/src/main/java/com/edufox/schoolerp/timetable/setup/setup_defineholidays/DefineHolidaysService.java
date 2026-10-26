package com.edufox.schoolerp.timetable.setup.setup_defineholidays;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Setup/DefineHolidays.jsx. */
@Service("page_timetable_setup_setup_defineholidays_defineholidaysservice_java")
public class DefineHolidaysService extends PageArtifactService<DefineHolidaysRepository> {
    public DefineHolidaysService(DefineHolidaysRepository repository) {
        super(repository);
    }
}