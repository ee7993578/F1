package com.edufox.schoolerp.communication.report.report_emailreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Communication/Report/EmailReport.jsx. */
@RestController("page_communication_report_report_emailreport_emailreportcontroller_java")
@RequestMapping("/api/page-artifacts/communication/report/report_emailreport")
public class EmailReportController extends PageArtifactController<EmailReportService> {
    public EmailReportController(EmailReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}