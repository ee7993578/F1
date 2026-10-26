package com.edufox.schoolerp.examination.report.report_overallexaminationreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/OverallExaminationReport.jsx. */
@RestController("page_examination_report_report_overallexaminationreport_overallexaminationreportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_overallexaminationreport")
public class OverallExaminationReportController extends PageArtifactController<OverallExaminationReportService> {
    public OverallExaminationReportController(OverallExaminationReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}