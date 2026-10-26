package com.edufox.schoolerp.uploads.work.work_academicplanner;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Work/AcademicPlanner.jsx. */
@Service("page_uploads_work_work_academicplanner_academicplannerservice_java")
public class AcademicPlannerService extends PageArtifactService<AcademicPlannerRepository> {
    public AcademicPlannerService(AcademicPlannerRepository repository) {
        super(repository);
    }
}