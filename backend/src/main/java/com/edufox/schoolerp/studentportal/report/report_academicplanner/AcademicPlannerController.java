package com.edufox.schoolerp.studentportal.report.report_academicplanner;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/AcademicPlanner.jsx. */
@RestController("page_studentportal_report_report_academicplanner_academicplannercontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_academicplanner")
public class AcademicPlannerController extends PageArtifactController<AcademicPlannerService> {
    public AcademicPlannerController(AcademicPlannerService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}