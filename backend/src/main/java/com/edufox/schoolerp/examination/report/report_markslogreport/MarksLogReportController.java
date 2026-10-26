package com.edufox.schoolerp.examination.report.report_markslogreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/MarksLogReport.jsx. */
@RestController("page_examination_report_report_markslogreport_markslogreportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_markslogreport")
public class MarksLogReportController extends PageArtifactController<MarksLogReportService> {
    public MarksLogReportController(MarksLogReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}