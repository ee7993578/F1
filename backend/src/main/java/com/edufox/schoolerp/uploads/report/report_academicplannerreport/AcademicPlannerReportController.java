package com.edufox.schoolerp.uploads.report.report_academicplannerreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Report/AcademicPlannerReport.jsx. */
@RestController("page_uploads_report_report_academicplannerreport_academicplannerreportcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/report/report_academicplannerreport")
public class AcademicPlannerReportController extends PageArtifactController<AcademicPlannerReportService> {
    public AcademicPlannerReportController(AcademicPlannerReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}