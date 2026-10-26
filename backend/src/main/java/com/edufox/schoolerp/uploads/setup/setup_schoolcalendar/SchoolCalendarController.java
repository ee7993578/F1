package com.edufox.schoolerp.uploads.setup.setup_schoolcalendar;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Setup/SchoolCalendar.jsx. */
@RestController("page_uploads_setup_setup_schoolcalendar_schoolcalendarcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/setup/setup_schoolcalendar")
public class SchoolCalendarController extends PageArtifactController<SchoolCalendarService> {
    public SchoolCalendarController(SchoolCalendarService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}