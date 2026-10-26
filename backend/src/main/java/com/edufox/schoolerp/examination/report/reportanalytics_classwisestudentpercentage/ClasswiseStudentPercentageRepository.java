package com.edufox.schoolerp.examination.report.reportanalytics_classwisestudentpercentage;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/ReportAnalytics/ClasswiseStudentPercentage.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reportanalytics_classwisestudentpercentage_classwisestudentpercentagerepository_java")
public class ClasswiseStudentPercentageRepository extends PageArtifactRepository {
    public ClasswiseStudentPercentageRepository() {
        super("ExamMaster", "report", "ClasswiseStudentPercentage", "ExamMaster/ReportAnalytics/ClasswiseStudentPercentage.jsx");
    }
}