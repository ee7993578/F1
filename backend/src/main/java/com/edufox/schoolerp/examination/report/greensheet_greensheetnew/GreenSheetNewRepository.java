package com.edufox.schoolerp.examination.report.greensheet_greensheetnew;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Greensheet/GreenSheetNew.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_greensheet_greensheetnew_greensheetnewrepository_java")
public class GreenSheetNewRepository extends PageArtifactRepository {
    public GreenSheetNewRepository() {
        super("ExamMaster", "report", "GreenSheetNew", "ExamMaster/Greensheet/GreenSheetNew.jsx");
    }
}