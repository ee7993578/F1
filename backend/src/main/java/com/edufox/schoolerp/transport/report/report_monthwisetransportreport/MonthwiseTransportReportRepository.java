package com.edufox.schoolerp.transport.report.report_monthwisetransportreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Report/MonthwiseTransportReport.jsx. */
@Repository("page_com_edufox_schoolerp_transport_report_report_monthwisetransportreport_monthwisetransportreportrepository_java")
public class MonthwiseTransportReportRepository extends PageArtifactRepository {
    public MonthwiseTransportReportRepository() {
        super("Transport", "report", "MonthwiseTransportReport", "Transport/Report/MonthwiseTransportReport.jsx");
    }
}