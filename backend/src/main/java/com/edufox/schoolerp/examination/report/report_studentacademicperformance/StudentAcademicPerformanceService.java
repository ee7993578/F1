package com.edufox.schoolerp.examination.report.report_studentacademicperformance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/StudentAcademicPerformance.jsx. */
@Service("page_examination_report_report_studentacademicperformance_studentacademicperformanceservice_java")
public class StudentAcademicPerformanceService extends PageArtifactService<StudentAcademicPerformanceRepository> {
    public StudentAcademicPerformanceService(StudentAcademicPerformanceRepository repository) {
        super(repository);
    }
}