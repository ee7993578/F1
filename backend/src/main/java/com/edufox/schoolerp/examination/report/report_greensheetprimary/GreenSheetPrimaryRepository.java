package com.edufox.schoolerp.examination.report.report_greensheetprimary;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/GreenSheetPrimary.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_greensheetprimary_greensheetprimaryrepository_java")
public class GreenSheetPrimaryRepository extends PageArtifactRepository {
    public GreenSheetPrimaryRepository() {
        super("ExamMaster", "report", "GreenSheetPrimary", "ExamMaster/Report/GreenSheetPrimary.jsx");
    }
}