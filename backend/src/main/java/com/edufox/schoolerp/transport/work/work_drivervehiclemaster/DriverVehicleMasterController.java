package com.edufox.schoolerp.transport.work.work_drivervehiclemaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Transport/Work/DriverVehicleMaster.jsx. */
@RestController("page_transport_work_work_drivervehiclemaster_drivervehiclemastercontroller_java")
@RequestMapping("/api/page-artifacts/transport/work/work_drivervehiclemaster")
public class DriverVehicleMasterController extends PageArtifactController<DriverVehicleMasterService> {
    public DriverVehicleMasterController(DriverVehicleMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}