package com.edufox.schoolerp.transport.report.report_transportprivatestudentsreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Report/TransportPrivateStudentsReport.jsx. */
@Service("page_transport_report_report_transportprivatestudentsreport_transportprivatestudentsreportservice_java")
public class TransportPrivateStudentsReportService extends PageArtifactService<TransportPrivateStudentsReportRepository> {
    public TransportPrivateStudentsReportService(TransportPrivateStudentsReportRepository repository) {
        super(repository);
    }
}