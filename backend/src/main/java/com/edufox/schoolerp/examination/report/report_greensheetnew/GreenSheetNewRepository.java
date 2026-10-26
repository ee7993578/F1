package com.edufox.schoolerp.examination.report.report_greensheetnew;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/GreenSheetNew.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_greensheetnew_greensheetnewrepository_java")
public class GreenSheetNewRepository extends PageArtifactRepository {
    public GreenSheetNewRepository() {
        super("ExamMaster", "report", "GreenSheetNew", "ExamMaster/Report/GreenSheetNew.jsx");
    }
}