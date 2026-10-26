package com.edufox.schoolerp.examination.report.greensheet_expectedschoolaveragereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Greensheet/ExpectedSchoolAverageReport.jsx. */
@RestController("page_examination_report_greensheet_expectedschoolaveragereport_expectedschoolaveragereportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/greensheet_expectedschoolaveragereport")
public class ExpectedSchoolAverageReportController extends PageArtifactController<ExpectedSchoolAverageReportService> {
    public ExpectedSchoolAverageReportController(ExpectedSchoolAverageReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}