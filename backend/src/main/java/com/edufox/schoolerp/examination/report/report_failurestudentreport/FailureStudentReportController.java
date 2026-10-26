package com.edufox.schoolerp.examination.report.report_failurestudentreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/FailureStudentReport.jsx. */
@RestController("page_examination_report_report_failurestudentreport_failurestudentreportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_failurestudentreport")
public class FailureStudentReportController extends PageArtifactController<FailureStudentReportService> {
    public FailureStudentReportController(FailureStudentReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}