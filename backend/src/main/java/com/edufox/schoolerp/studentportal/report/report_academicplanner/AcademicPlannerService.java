package com.edufox.schoolerp.studentportal.report.report_academicplanner;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/AcademicPlanner.jsx. */
@Service("page_studentportal_report_report_academicplanner_academicplannerservice_java")
public class AcademicPlannerService extends PageArtifactService<AcademicPlannerRepository> {
    public AcademicPlannerService(AcademicPlannerRepository repository) {
        super(repository);
    }
}