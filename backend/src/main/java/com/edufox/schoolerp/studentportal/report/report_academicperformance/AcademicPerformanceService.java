package com.edufox.schoolerp.studentportal.report.report_academicperformance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/AcademicPerformance.jsx. */
@Service("page_studentportal_report_report_academicperformance_academicperformanceservice_java")
public class AcademicPerformanceService extends PageArtifactService<AcademicPerformanceRepository> {
    public AcademicPerformanceService(AcademicPerformanceRepository repository) {
        super(repository);
    }
}