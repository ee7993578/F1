package com.edufox.schoolerp.hostel.work.work_hostelfees;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Hostel/Work/HostelFees.jsx. */
@Service("page_hostel_work_work_hostelfees_hostelfeesservice_java")
public class HostelFeesService extends PageArtifactService<HostelFeesRepository> {
    public HostelFeesService(HostelFeesRepository repository) {
        super(repository);
    }
}