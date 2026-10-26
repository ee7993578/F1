package com.edufox.schoolerp.uploads.report.report_assignmentreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Report/AssignmentReport.jsx. */
@RestController("page_uploads_report_report_assignmentreport_assignmentreportcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/report/report_assignmentreport")
public class AssignmentReportController extends PageArtifactController<AssignmentReportService> {
    public AssignmentReportController(AssignmentReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}