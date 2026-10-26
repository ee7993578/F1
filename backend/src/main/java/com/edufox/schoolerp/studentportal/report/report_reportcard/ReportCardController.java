package com.edufox.schoolerp.studentportal.report.report_reportcard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/ReportCard.jsx. */
@RestController("page_studentportal_report_report_reportcard_reportcardcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_reportcard")
public class ReportCardController extends PageArtifactController<ReportCardService> {
    public ReportCardController(ReportCardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}