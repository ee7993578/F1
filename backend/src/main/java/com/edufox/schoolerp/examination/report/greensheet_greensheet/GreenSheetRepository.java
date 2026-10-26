package com.edufox.schoolerp.examination.report.greensheet_greensheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Greensheet/GreenSheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_greensheet_greensheet_greensheetrepository_java")
public class GreenSheetRepository extends PageArtifactRepository {
    public GreenSheetRepository() {
        super("ExamMaster", "report", "GreenSheet", "ExamMaster/Greensheet/GreenSheet.jsx");
    }
}