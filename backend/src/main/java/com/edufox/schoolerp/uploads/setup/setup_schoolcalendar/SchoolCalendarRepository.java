package com.edufox.schoolerp.uploads.setup.setup_schoolcalendar;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Setup/SchoolCalendar.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_setup_setup_schoolcalendar_schoolcalendarrepository_java")
public class SchoolCalendarRepository extends PageArtifactRepository {
    public SchoolCalendarRepository() {
        super("Uploads", "setup", "SchoolCalendar", "Uploads/Setup/SchoolCalendar.jsx");
    }
}