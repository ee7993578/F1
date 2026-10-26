package com.edufox.schoolerp.studentportal.report.report_holidaylist;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/HolidayList.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_holidaylist_holidaylistrepository_java")
public class HolidayListRepository extends PageArtifactRepository {
    public HolidayListRepository() {
        super("StundentPages", "report", "HolidayList", "StundentPages/Report/HolidayList.jsx");
    }
}