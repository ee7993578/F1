package com.edufox.schoolerp.transport.report.report_assignroute;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Report/AssignRoute.jsx. */
@Service("page_transport_report_report_assignroute_assignrouteservice_java")
public class AssignRouteService extends PageArtifactService<AssignRouteRepository> {
    public AssignRouteService(AssignRouteRepository repository) {
        super(repository);
    }
}