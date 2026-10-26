package com.edufox.schoolerp.hostel.work.work_assignhostel;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Hostel/Work/AssignHostel.jsx. */
@Service("page_hostel_work_work_assignhostel_assignhostelservice_java")
public class AssignHostelService extends PageArtifactService<AssignHostelRepository> {
    public AssignHostelService(AssignHostelRepository repository) {
        super(repository);
    }
}