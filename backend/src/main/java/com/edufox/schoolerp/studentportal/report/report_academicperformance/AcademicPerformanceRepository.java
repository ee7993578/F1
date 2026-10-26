package com.edufox.schoolerp.studentportal.report.report_academicperformance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/AcademicPerformance.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_academicperformance_academicperformancerepository_java")
public class AcademicPerformanceRepository extends PageArtifactRepository {
    public AcademicPerformanceRepository() {
        super("StundentPages", "report", "AcademicPerformance", "StundentPages/Report/AcademicPerformance.jsx");
    }
}