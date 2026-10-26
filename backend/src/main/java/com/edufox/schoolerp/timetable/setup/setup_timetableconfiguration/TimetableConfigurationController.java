package com.edufox.schoolerp.timetable.setup.setup_timetableconfiguration;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Setup/TimetableConfiguration.jsx. */
@RestController("page_timetable_setup_setup_timetableconfiguration_timetableconfigurationcontroller_java")
@RequestMapping("/api/page-artifacts/timetable/setup/setup_timetableconfiguration")
public class TimetableConfigurationController extends PageArtifactController<TimetableConfigurationService> {
    public TimetableConfigurationController(TimetableConfigurationService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}