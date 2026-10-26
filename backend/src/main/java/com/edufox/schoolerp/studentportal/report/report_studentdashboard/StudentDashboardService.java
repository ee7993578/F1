package com.edufox.schoolerp.studentportal.report.report_studentdashboard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/StudentDashboard.jsx. */
@Service("page_studentportal_report_report_studentdashboard_studentdashboardservice_java")
public class StudentDashboardService extends PageArtifactService<StudentDashboardRepository> {
    public StudentDashboardService(StudentDashboardRepository repository) {
        super(repository);
    }
}