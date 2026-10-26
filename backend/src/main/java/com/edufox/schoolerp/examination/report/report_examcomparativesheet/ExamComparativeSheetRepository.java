package com.edufox.schoolerp.examination.report.report_examcomparativesheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ExamComparativeSheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_examcomparativesheet_examcomparativesheetrepository_java")
public class ExamComparativeSheetRepository extends PageArtifactRepository {
    public ExamComparativeSheetRepository() {
        super("ExamMaster", "report", "ExamComparativeSheet", "ExamMaster/Report/ExamComparativeSheet.jsx");
    }
}