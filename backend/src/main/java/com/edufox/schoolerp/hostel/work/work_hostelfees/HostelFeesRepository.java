package com.edufox.schoolerp.hostel.work.work_hostelfees;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Hostel/Work/HostelFees.jsx. */
@Repository("page_com_edufox_schoolerp_hostel_work_work_hostelfees_hostelfeesrepository_java")
public class HostelFeesRepository extends PageArtifactRepository {
    public HostelFeesRepository() {
        super("Hostel", "work", "HostelFees", "Hostel/Work/HostelFees.jsx");
    }
}