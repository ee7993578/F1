package com.edufox.schoolerp.transport.report.report_transportprivatestudentsreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Report/TransportPrivateStudentsReport.jsx. */
@Repository("page_com_edufox_schoolerp_transport_report_report_transportprivatestudentsreport_transportprivatestudentsreportrepository_java")
public class TransportPrivateStudentsReportRepository extends PageArtifactRepository {
    public TransportPrivateStudentsReportRepository() {
        super("Transport", "report", "TransportPrivateStudentsReport", "Transport/Report/TransportPrivateStudentsReport.jsx");
    }
}