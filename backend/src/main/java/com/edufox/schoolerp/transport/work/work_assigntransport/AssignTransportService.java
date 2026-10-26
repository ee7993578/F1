package com.edufox.schoolerp.transport.work.work_assigntransport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Work/AssignTransport.jsx. */
@Service("page_transport_work_work_assigntransport_assigntransportservice_java")
public class AssignTransportService extends PageArtifactService<AssignTransportRepository> {
    public AssignTransportService(AssignTransportRepository repository) {
        super(repository);
    }
}