package com.edufox.schoolerp.examination.report.reportcard_reportcard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Reportcard/ReportCard.jsx. */
@RestController("page_examination_report_reportcard_reportcard_reportcardcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/reportcard_reportcard")
public class ReportCardController extends PageArtifactController<ReportCardService> {
    public ReportCardController(ReportCardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}