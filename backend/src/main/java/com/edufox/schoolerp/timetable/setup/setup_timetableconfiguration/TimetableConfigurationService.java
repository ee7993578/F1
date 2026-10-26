package com.edufox.schoolerp.timetable.setup.setup_timetableconfiguration;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for TimeTable/Setup/TimetableConfiguration.jsx. */
@Service("page_timetable_setup_setup_timetableconfiguration_timetableconfigurationservice_java")
public class TimetableConfigurationService extends PageArtifactService<TimetableConfigurationRepository> {
    public TimetableConfigurationService(TimetableConfigurationRepository repository) {
        super(repository);
    }
}