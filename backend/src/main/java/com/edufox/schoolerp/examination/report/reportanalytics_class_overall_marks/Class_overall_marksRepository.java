package com.edufox.schoolerp.examination.report.reportanalytics_class_overall_marks;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/ReportAnalytics/Class_overall_marks.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reportanalytics_class_overall_marks_class_overall_marksrepository_java")
public class Class_overall_marksRepository extends PageArtifactRepository {
    public Class_overall_marksRepository() {
        super("ExamMaster", "report", "Class_overall_marks", "ExamMaster/ReportAnalytics/Class_overall_marks.jsx");
    }
}