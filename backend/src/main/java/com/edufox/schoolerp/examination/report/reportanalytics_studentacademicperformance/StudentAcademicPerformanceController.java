package com.edufox.schoolerp.examination.report.reportanalytics_studentacademicperformance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/ReportAnalytics/StudentAcademicPerformance.jsx. */
@RestController("page_examination_report_reportanalytics_studentacademicperformance_studentacademicperformancecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/reportanalytics_studentacademicperformance")
public class StudentAcademicPerformanceController extends PageArtifactController<StudentAcademicPerformanceService> {
    public StudentAcademicPerformanceController(StudentAcademicPerformanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}