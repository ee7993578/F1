package com.edufox.schoolerp.examination.report.report_studentacademicperformance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/StudentAcademicPerformance.jsx. */
@RestController("page_examination_report_report_studentacademicperformance_studentacademicperformancecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_studentacademicperformance")
public class StudentAcademicPerformanceController extends PageArtifactController<StudentAcademicPerformanceService> {
    public StudentAcademicPerformanceController(StudentAcademicPerformanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}