package com.edufox.schoolerp.examination.report.greensheet_expectedboardaveragereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Greensheet/ExpectedBoardAverageReport.jsx. */
@RestController("page_examination_report_greensheet_expectedboardaveragereport_expectedboardaveragereportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/greensheet_expectedboardaveragereport")
public class ExpectedBoardAverageReportController extends PageArtifactController<ExpectedBoardAverageReportService> {
    public ExpectedBoardAverageReportController(ExpectedBoardAverageReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}