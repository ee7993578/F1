package com.edufox.schoolerp.examination.report.report_subjectreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/SubjectReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_subjectreport_subjectreportrepository_java")
public class SubjectReportRepository extends PageArtifactRepository {
    public SubjectReportRepository() {
        super("ExamMaster", "report", "SubjectReport", "ExamMaster/Report/SubjectReport.jsx");
    }
}