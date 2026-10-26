package com.edufox.schoolerp.studentportal.report.report_academicperformance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/AcademicPerformance.jsx. */
@RestController("page_studentportal_report_report_academicperformance_academicperformancecontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_academicperformance")
public class AcademicPerformanceController extends PageArtifactController<AcademicPerformanceService> {
    public AcademicPerformanceController(AcademicPerformanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}