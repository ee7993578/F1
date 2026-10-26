package com.edufox.schoolerp.studentportal.report.report_holidaylist;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/HolidayList.jsx. */
@Service("page_studentportal_report_report_holidaylist_holidaylistservice_java")
public class HolidayListService extends PageArtifactService<HolidayListRepository> {
    public HolidayListService(HolidayListRepository repository) {
        super(repository);
    }
}