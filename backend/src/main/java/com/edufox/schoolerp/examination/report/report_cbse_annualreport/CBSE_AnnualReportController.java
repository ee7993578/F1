package com.edufox.schoolerp.examination.report.report_cbse_annualreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/CBSE_AnnualReport.jsx. */
@RestController("page_examination_report_report_cbse_annualreport_cbse_annualreportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_cbse_annualreport")
public class CBSE_AnnualReportController extends PageArtifactController<CBSE_AnnualReportService> {
    public CBSE_AnnualReportController(CBSE_AnnualReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}