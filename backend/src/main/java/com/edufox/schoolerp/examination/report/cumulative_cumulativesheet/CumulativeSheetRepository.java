package com.edufox.schoolerp.examination.report.cumulative_cumulativesheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/cumulative/CumulativeSheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_cumulative_cumulativesheet_cumulativesheetrepository_java")
public class CumulativeSheetRepository extends PageArtifactRepository {
    public CumulativeSheetRepository() {
        super("ExamMaster", "report", "CumulativeSheet", "ExamMaster/cumulative/CumulativeSheet.jsx");
    }
}