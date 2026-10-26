package com.edufox.schoolerp.studentportal.report.report_holidayhomework;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/HolidayHomework.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_holidayhomework_holidayhomeworkrepository_java")
public class HolidayHomeworkRepository extends PageArtifactRepository {
    public HolidayHomeworkRepository() {
        super("StundentPages", "report", "HolidayHomework", "StundentPages/Report/HolidayHomework.jsx");
    }
}