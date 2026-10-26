package com.edufox.schoolerp.transport.work.work_vehiclemaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Work/VehicleMaster.jsx. */
@RestController("page_transport_work_work_vehiclemaster_vehiclemastercontroller_java")
@RequestMapping("/api/page-artifacts/transport/work/work_vehiclemaster")
public class VehicleMasterController extends PageArtifactController<VehicleMasterService> {
    public VehicleMasterController(VehicleMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}