package com.edufox.schoolerp.communication.report.report_smsreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Communication/Report/SmsReport.jsx. */
@RestController("page_communication_report_report_smsreport_smsreportcontroller_java")
@RequestMapping("/api/page-artifacts/communication/report/report_smsreport")
public class SmsReportController extends PageArtifactController<SmsReportService> {
    public SmsReportController(SmsReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}