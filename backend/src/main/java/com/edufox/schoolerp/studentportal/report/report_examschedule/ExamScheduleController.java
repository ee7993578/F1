package com.edufox.schoolerp.studentportal.report.report_examschedule;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/ExamSchedule.jsx. */
@RestController("page_studentportal_report_report_examschedule_examschedulecontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_examschedule")
public class ExamScheduleController extends PageArtifactController<ExamScheduleService> {
    public ExamScheduleController(ExamScheduleService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}