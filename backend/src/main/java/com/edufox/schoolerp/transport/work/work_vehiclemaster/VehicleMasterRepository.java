package com.edufox.schoolerp.transport.work.work_vehiclemaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Work/VehicleMaster.jsx. */
@Repository("page_com_edufox_schoolerp_transport_work_work_vehiclemaster_vehiclemasterrepository_java")
public class VehicleMasterRepository extends PageArtifactRepository {
    public VehicleMasterRepository() {
        super("Transport", "work", "VehicleMaster", "Transport/Work/VehicleMaster.jsx");
    }
}