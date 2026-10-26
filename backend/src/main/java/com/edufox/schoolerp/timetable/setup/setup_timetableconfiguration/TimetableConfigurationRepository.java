package com.edufox.schoolerp.timetable.setup.setup_timetableconfiguration;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for TimeTable/Setup/TimetableConfiguration.jsx. */
@Repository("page_com_edufox_schoolerp_timetable_setup_setup_timetableconfiguration_timetableconfigurationrepository_java")
public class TimetableConfigurationRepository extends PageArtifactRepository {
    public TimetableConfigurationRepository() {
        super("TimeTable", "setup", "TimetableConfiguration", "TimeTable/Setup/TimetableConfiguration.jsx");
    }
}