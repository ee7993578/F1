package com.edufox.schoolerp.transport.report.report_studenttransportreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Report/StudentTransportReport.jsx. */
@Repository("page_com_edufox_schoolerp_transport_report_report_studenttransportreport_studenttransportreportrepository_java")
public class StudentTransportReportRepository extends PageArtifactRepository {
    public StudentTransportReportRepository() {
        super("Transport", "report", "StudentTransportReport", "Transport/Report/StudentTransportReport.jsx");
    }
}