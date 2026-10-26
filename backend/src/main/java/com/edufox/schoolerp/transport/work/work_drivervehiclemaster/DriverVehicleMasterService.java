package com.edufox.schoolerp.transport.work.work_drivervehiclemaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Work/DriverVehicleMaster.jsx. */
@Service("page_transport_work_work_drivervehiclemaster_drivervehiclemasterservice_java")
public class DriverVehicleMasterService extends PageArtifactService<DriverVehicleMasterRepository> {
    public DriverVehicleMasterService(DriverVehicleMasterRepository repository) {
        super(repository);
    }
}