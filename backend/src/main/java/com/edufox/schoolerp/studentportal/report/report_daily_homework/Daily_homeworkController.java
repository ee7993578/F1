package com.edufox.schoolerp.studentportal.report.report_daily_homework;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/Daily_homework.jsx. */
@RestController("page_studentportal_report_report_daily_homework_daily_homeworkcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_daily_homework")
public class Daily_homeworkController extends PageArtifactController<Daily_homeworkService> {
    public Daily_homeworkController(Daily_homeworkService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}