package com.edufox.schoolerp.transport.report.report_transportprivatestudentsreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Report/TransportPrivateStudentsReport.jsx. */
@RestController("page_transport_report_report_transportprivatestudentsreport_transportprivatestudentsreportcontroller_java")
@RequestMapping("/api/page-artifacts/transport/report/report_transportprivatestudentsreport")
public class TransportPrivateStudentsReportController extends PageArtifactController<TransportPrivateStudentsReportService> {
    public TransportPrivateStudentsReportController(TransportPrivateStudentsReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}