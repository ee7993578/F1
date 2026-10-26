package com.edufox.schoolerp.examination.report.reportanalytics_subjectreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/ReportAnalytics/SubjectReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reportanalytics_subjectreport_subjectreportrepository_java")
public class SubjectReportRepository extends PageArtifactRepository {
    public SubjectReportRepository() {
        super("ExamMaster", "report", "SubjectReport", "ExamMaster/ReportAnalytics/SubjectReport.jsx");
    }
}