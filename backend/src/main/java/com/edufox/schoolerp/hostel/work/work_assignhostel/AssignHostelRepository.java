package com.edufox.schoolerp.hostel.work.work_assignhostel;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Hostel/Work/AssignHostel.jsx. */
@Repository("page_com_edufox_schoolerp_hostel_work_work_assignhostel_assignhostelrepository_java")
public class AssignHostelRepository extends PageArtifactRepository {
    public AssignHostelRepository() {
        super("Hostel", "work", "AssignHostel", "Hostel/Work/AssignHostel.jsx");
    }
}