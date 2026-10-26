package com.edufox.schoolerp.examination.report.report_studentacademicperformance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/StudentAcademicPerformance.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_studentacademicperformance_studentacademicperformancerepository_java")
public class StudentAcademicPerformanceRepository extends PageArtifactRepository {
    public StudentAcademicPerformanceRepository() {
        super("ExamMaster", "report", "StudentAcademicPerformance", "ExamMaster/Report/StudentAcademicPerformance.jsx");
    }
}