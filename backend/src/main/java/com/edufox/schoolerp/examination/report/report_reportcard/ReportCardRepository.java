package com.edufox.schoolerp.examination.report.report_reportcard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ReportCard.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_reportcard_reportcardrepository_java")
public class ReportCardRepository extends PageArtifactRepository {
    public ReportCardRepository() {
        super("ExamMaster", "report", "ReportCard", "ExamMaster/Report/ReportCard.jsx");
    }
}