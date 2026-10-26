package com.edufox.schoolerp.studentportal.report.report_academicplanner;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/AcademicPlanner.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_academicplanner_academicplannerrepository_java")
public class AcademicPlannerRepository extends PageArtifactRepository {
    public AcademicPlannerRepository() {
        super("StundentPages", "report", "AcademicPlanner", "StundentPages/Report/AcademicPlanner.jsx");
    }
}