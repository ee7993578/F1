package com.edufox.schoolerp.transport.work.work_assigntransport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Work/AssignTransport.jsx. */
@RestController("page_transport_work_work_assigntransport_assigntransportcontroller_java")
@RequestMapping("/api/page-artifacts/transport/work/work_assigntransport")
public class AssignTransportController extends PageArtifactController<AssignTransportService> {
    public AssignTransportController(AssignTransportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}