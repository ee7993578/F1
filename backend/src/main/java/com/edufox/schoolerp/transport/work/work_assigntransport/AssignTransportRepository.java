package com.edufox.schoolerp.transport.work.work_assigntransport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Work/AssignTransport.jsx. */
@Repository("page_com_edufox_schoolerp_transport_work_work_assigntransport_assigntransportrepository_java")
public class AssignTransportRepository extends PageArtifactRepository {
    public AssignTransportRepository() {
        super("Transport", "work", "AssignTransport", "Transport/Work/AssignTransport.jsx");
    }
}