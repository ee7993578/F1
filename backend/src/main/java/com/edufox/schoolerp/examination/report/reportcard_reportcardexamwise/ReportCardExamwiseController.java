package com.edufox.schoolerp.examination.report.reportcard_reportcardexamwise;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Reportcard/ReportCardExamwise.jsx. */
@RestController("page_examination_report_reportcard_reportcardexamwise_reportcardexamwisecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/reportcard_reportcardexamwise")
public class ReportCardExamwiseController extends PageArtifactController<ReportCardExamwiseService> {
    public ReportCardExamwiseController(ReportCardExamwiseService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}