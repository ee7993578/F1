package com.edufox.schoolerp.examination.report.greensheet_examcomparativesheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Greensheet/ExamComparativeSheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_greensheet_examcomparativesheet_examcomparativesheetrepository_java")
public class ExamComparativeSheetRepository extends PageArtifactRepository {
    public ExamComparativeSheetRepository() {
        super("ExamMaster", "report", "ExamComparativeSheet", "ExamMaster/Greensheet/ExamComparativeSheet.jsx");
    }
}