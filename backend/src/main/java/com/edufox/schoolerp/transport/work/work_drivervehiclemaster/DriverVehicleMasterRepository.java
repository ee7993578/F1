package com.edufox.schoolerp.transport.work.work_drivervehiclemaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Transport/Work/DriverVehicleMaster.jsx. */
@Repository("page_com_edufox_schoolerp_transport_work_work_drivervehiclemaster_drivervehiclemasterrepository_java")
public class DriverVehicleMasterRepository extends PageArtifactRepository {
    public DriverVehicleMasterRepository() {
        super("Transport", "work", "DriverVehicleMaster", "Transport/Work/DriverVehicleMaster.jsx");
    }
}