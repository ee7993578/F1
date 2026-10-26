package com.edufox.schoolerp.transport.report.report_assignroute;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Report/AssignRoute.jsx. */
@RestController("page_transport_report_report_assignroute_assignroutecontroller_java")
@RequestMapping("/api/page-artifacts/transport/report/report_assignroute")
public class AssignRouteController extends PageArtifactController<AssignRouteService> {
    public AssignRouteController(AssignRouteService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}