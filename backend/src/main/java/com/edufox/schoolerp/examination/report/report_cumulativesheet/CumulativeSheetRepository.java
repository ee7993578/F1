package com.edufox.schoolerp.examination.report.report_cumulativesheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/CumulativeSheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_cumulativesheet_cumulativesheetrepository_java")
public class CumulativeSheetRepository extends PageArtifactRepository {
    public CumulativeSheetRepository() {
        super("ExamMaster", "report", "CumulativeSheet", "ExamMaster/Report/CumulativeSheet.jsx");
    }
}