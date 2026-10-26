package com.edufox.schoolerp.certificate.report.report_tcreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Certificates/Report/TCReport.jsx. */
@RestController("page_certificate_report_report_tcreport_tcreportcontroller_java")
@RequestMapping("/api/page-artifacts/certificates/report/report_tcreport")
public class TCReportController extends PageArtifactController<TCReportService> {
    public TCReportController(TCReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}