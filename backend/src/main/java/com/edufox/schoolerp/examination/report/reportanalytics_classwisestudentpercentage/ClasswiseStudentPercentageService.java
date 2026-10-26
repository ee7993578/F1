package com.edufox.schoolerp.examination.report.reportanalytics_classwisestudentpercentage;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/ReportAnalytics/ClasswiseStudentPercentage.jsx. */
@Service("page_examination_report_reportanalytics_classwisestudentpercentage_classwisestudentpercentageservice_java")
public class ClasswiseStudentPercentageService extends PageArtifactService<ClasswiseStudentPercentageRepository> {
    public ClasswiseStudentPercentageService(ClasswiseStudentPercentageRepository repository) {
        super(repository);
    }
}