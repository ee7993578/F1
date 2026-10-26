package com.edufox.schoolerp.transport.report.report_studenttransportreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Report/StudentTransportReport.jsx. */
@RestController("page_transport_report_report_studenttransportreport_studenttransportreportcontroller_java")
@RequestMapping("/api/page-artifacts/transport/report/report_studenttransportreport")
public class StudentTransportReportController extends PageArtifactController<StudentTransportReportService> {
    public StudentTransportReportController(StudentTransportReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}