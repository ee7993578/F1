package com.edufox.schoolerp.timetable.report.report_facultyfreeperiod;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for TimeTable/Report/FacultyFreePeriod.jsx. */
@RestController("page_timetable_report_report_facultyfreeperiod_facultyfreeperiodcontroller_java")
@RequestMapping("/api/page-artifacts/timetable/report/report_facultyfreeperiod")
public class FacultyFreePeriodController extends PageArtifactController<FacultyFreePeriodService> {
    public FacultyFreePeriodController(FacultyFreePeriodService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}