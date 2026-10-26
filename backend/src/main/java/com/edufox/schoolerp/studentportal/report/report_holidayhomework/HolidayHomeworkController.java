package com.edufox.schoolerp.studentportal.report.report_holidayhomework;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/HolidayHomework.jsx. */
@RestController("page_studentportal_report_report_holidayhomework_holidayhomeworkcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_holidayhomework")
public class HolidayHomeworkController extends PageArtifactController<HolidayHomeworkService> {
    public HolidayHomeworkController(HolidayHomeworkService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}