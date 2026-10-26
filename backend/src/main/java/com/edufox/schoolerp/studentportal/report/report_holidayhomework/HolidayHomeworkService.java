package com.edufox.schoolerp.studentportal.report.report_holidayhomework;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/HolidayHomework.jsx. */
@Service("page_studentportal_report_report_holidayhomework_holidayhomeworkservice_java")
public class HolidayHomeworkService extends PageArtifactService<HolidayHomeworkRepository> {
    public HolidayHomeworkService(HolidayHomeworkRepository repository) {
        super(repository);
    }
}