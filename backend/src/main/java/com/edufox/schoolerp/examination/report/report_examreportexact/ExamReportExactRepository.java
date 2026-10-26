package com.edufox.schoolerp.examination.report.report_examreportexact;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ExamReportExact.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_examreportexact_examreportexactrepository_java")
public class ExamReportExactRepository extends PageArtifactRepository {
    public ExamReportExactRepository() {
        super("ExamMaster", "report", "ExamReportExact", "ExamMaster/Report/ExamReportExact.jsx");
    }
}