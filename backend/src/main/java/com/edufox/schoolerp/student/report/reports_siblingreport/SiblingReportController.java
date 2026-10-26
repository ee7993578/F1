package com.edufox.schoolerp.student.report.reports_siblingreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/SiblingReport.jsx. */
@RestController("page_student_report_reports_siblingreport_siblingreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_siblingreport")
public class SiblingReportController extends PageArtifactController<SiblingReportService> {
    public SiblingReportController(SiblingReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}