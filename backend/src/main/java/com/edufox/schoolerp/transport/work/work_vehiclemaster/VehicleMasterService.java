package com.edufox.schoolerp.transport.work.work_vehiclemaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Transport/Work/VehicleMaster.jsx. */
@Service("page_transport_work_work_vehiclemaster_vehiclemasterservice_java")
public class VehicleMasterService extends PageArtifactService<VehicleMasterRepository> {
    public VehicleMasterService(VehicleMasterRepository repository) {
        super(repository);
    }
}