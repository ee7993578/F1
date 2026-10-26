package com.edufox.schoolerp.examination.report.report_greensheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/GreenSheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_greensheet_greensheetrepository_java")
public class GreenSheetRepository extends PageArtifactRepository {
    public GreenSheetRepository() {
        super("ExamMaster", "report", "GreenSheet", "ExamMaster/Report/GreenSheet.jsx");
    }
}