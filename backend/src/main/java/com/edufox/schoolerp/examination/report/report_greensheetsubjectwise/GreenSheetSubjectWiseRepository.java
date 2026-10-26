package com.edufox.schoolerp.examination.report.report_greensheetsubjectwise;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/GreenSheetSubjectWise.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_greensheetsubjectwise_greensheetsubjectwiserepository_java")
public class GreenSheetSubjectWiseRepository extends PageArtifactRepository {
    public GreenSheetSubjectWiseRepository() {
        super("ExamMaster", "report", "GreenSheetSubjectWise", "ExamMaster/Report/GreenSheetSubjectWise.jsx");
    }
}