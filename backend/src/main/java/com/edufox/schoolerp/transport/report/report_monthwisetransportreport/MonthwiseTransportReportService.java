package com.edufox.schoolerp.transport.report.report_monthwisetransportreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Report/MonthwiseTransportReport.jsx. */
@Service("page_transport_report_report_monthwisetransportreport_monthwisetransportreportservice_java")
public class MonthwiseTransportReportService extends PageArtifactService<MonthwiseTransportReportRepository> {
    public MonthwiseTransportReportService(MonthwiseTransportReportRepository repository) {
        super(repository);
    }
}