package com.edufox.schoolerp.examination.report.greensheet_greensheetsubjectwise;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Greensheet/GreenSheetSubjectWise.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_greensheet_greensheetsubjectwise_greensheetsubjectwiserepository_java")
public class GreenSheetSubjectWiseRepository extends PageArtifactRepository {
    public GreenSheetSubjectWiseRepository() {
        super("ExamMaster", "report", "GreenSheetSubjectWise", "ExamMaster/Greensheet/GreenSheetSubjectWise.jsx");
    }
}