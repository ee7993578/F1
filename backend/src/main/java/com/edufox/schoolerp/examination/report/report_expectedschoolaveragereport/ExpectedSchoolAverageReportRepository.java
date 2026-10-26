package com.edufox.schoolerp.examination.report.report_expectedschoolaveragereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/ExpectedSchoolAverageReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_expectedschoolaveragereport_expectedschoolaveragereportrepository_java")
public class ExpectedSchoolAverageReportRepository extends PageArtifactRepository {
    public ExpectedSchoolAverageReportRepository() {
        super("ExamMaster", "report", "ExpectedSchoolAverageReport", "ExamMaster/Report/ExpectedSchoolAverageReport.jsx");
    }
}