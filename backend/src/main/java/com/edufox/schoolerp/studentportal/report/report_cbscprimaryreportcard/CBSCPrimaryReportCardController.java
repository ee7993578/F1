package com.edufox.schoolerp.studentportal.report.report_cbscprimaryreportcard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/CBSCPrimaryReportCard.jsx. */
@RestController("page_studentportal_report_report_cbscprimaryreportcard_cbscprimaryreportcardcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_cbscprimaryreportcard")
public class CBSCPrimaryReportCardController extends PageArtifactController<CBSCPrimaryReportCardService> {
    public CBSCPrimaryReportCardController(CBSCPrimaryReportCardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}