package com.edufox.schoolerp.examination.report.reportanalytics_studentacademicperformance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/ReportAnalytics/StudentAcademicPerformance.jsx. */
@Service("page_examination_report_reportanalytics_studentacademicperformance_studentacademicperformanceservice_java")
public class StudentAcademicPerformanceService extends PageArtifactService<StudentAcademicPerformanceRepository> {
    public StudentAcademicPerformanceService(StudentAcademicPerformanceRepository repository) {
        super(repository);
    }
}