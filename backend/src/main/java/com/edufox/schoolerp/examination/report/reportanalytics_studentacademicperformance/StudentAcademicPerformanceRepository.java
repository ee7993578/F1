package com.edufox.schoolerp.examination.report.reportanalytics_studentacademicperformance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/ReportAnalytics/StudentAcademicPerformance.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reportanalytics_studentacademicperformance_studentacademicperformancerepository_java")
public class StudentAcademicPerformanceRepository extends PageArtifactRepository {
    public StudentAcademicPerformanceRepository() {
        super("ExamMaster", "report", "StudentAcademicPerformance", "ExamMaster/ReportAnalytics/StudentAcademicPerformance.jsx");
    }
}