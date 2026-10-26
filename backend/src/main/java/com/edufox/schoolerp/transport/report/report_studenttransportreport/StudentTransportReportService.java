package com.edufox.schoolerp.transport.report.report_studenttransportreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Report/StudentTransportReport.jsx. */
@Service("page_transport_report_report_studenttransportreport_studenttransportreportservice_java")
public class StudentTransportReportService extends PageArtifactService<StudentTransportReportRepository> {
    public StudentTransportReportService(StudentTransportReportRepository repository) {
        super(repository);
    }
}