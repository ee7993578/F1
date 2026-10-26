package com.edufox.schoolerp.examination.report.report_classwisestudentpercentage;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ClasswiseStudentPercentage.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_classwisestudentpercentage_classwisestudentpercentagerepository_java")
public class ClasswiseStudentPercentageRepository extends PageArtifactRepository {
    public ClasswiseStudentPercentageRepository() {
        super("ExamMaster", "report", "ClasswiseStudentPercentage", "ExamMaster/Report/ClasswiseStudentPercentage.jsx");
    }
}