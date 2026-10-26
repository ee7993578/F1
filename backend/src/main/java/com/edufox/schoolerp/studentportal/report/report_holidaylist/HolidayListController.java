package com.edufox.schoolerp.studentportal.report.report_holidaylist;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/HolidayList.jsx. */
@RestController("page_studentportal_report_report_holidaylist_holidaylistcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_holidaylist")
public class HolidayListController extends PageArtifactController<HolidayListService> {
    public HolidayListController(HolidayListService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}