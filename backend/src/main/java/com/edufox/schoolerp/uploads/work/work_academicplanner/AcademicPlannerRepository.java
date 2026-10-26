package com.edufox.schoolerp.uploads.work.work_academicplanner;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Work/AcademicPlanner.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_work_work_academicplanner_academicplannerrepository_java")
public class AcademicPlannerRepository extends PageArtifactRepository {
    public AcademicPlannerRepository() {
        super("Uploads", "work", "AcademicPlanner", "Uploads/Work/AcademicPlanner.jsx");
    }
}