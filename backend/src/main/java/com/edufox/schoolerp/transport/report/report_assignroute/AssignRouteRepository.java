package com.edufox.schoolerp.transport.report.report_assignroute;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Report/AssignRoute.jsx. */
@Repository("page_com_edufox_schoolerp_transport_report_report_assignroute_assignrouterepository_java")
public class AssignRouteRepository extends PageArtifactRepository {
    public AssignRouteRepository() {
        super("Transport", "report", "AssignRoute", "Transport/Report/AssignRoute.jsx");
    }
}