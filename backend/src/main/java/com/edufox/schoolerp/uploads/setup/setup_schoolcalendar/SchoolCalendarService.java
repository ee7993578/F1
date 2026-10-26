package com.edufox.schoolerp.uploads.setup.setup_schoolcalendar;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Setup/SchoolCalendar.jsx. */
@Service("page_uploads_setup_setup_schoolcalendar_schoolcalendarservice_java")
public class SchoolCalendarService extends PageArtifactService<SchoolCalendarRepository> {
    public SchoolCalendarService(SchoolCalendarRepository repository) {
        super(repository);
    }
}