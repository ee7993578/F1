package com.edufox.schoolerp.timetable.setup.setup_defineholidays;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Setup/DefineHolidays.jsx. */
@RestController("page_timetable_setup_setup_defineholidays_defineholidayscontroller_java")
@RequestMapping("/api/page-artifacts/timetable/setup/setup_defineholidays")
public class DefineHolidaysController extends PageArtifactController<DefineHolidaysService> {
    public DefineHolidaysController(DefineHolidaysService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}