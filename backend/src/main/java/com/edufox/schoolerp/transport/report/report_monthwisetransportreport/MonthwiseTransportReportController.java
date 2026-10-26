package com.edufox.schoolerp.transport.report.report_monthwisetransportreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Report/MonthwiseTransportReport.jsx. */
@RestController("page_transport_report_report_monthwisetransportreport_monthwisetransportreportcontroller_java")
@RequestMapping("/api/page-artifacts/transport/report/report_monthwisetransportreport")
public class MonthwiseTransportReportController extends PageArtifactController<MonthwiseTransportReportService> {
    public MonthwiseTransportReportController(MonthwiseTransportReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}