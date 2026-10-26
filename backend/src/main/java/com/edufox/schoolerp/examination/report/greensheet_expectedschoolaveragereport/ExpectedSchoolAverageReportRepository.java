package com.edufox.schoolerp.examination.report.greensheet_expectedschoolaveragereport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Greensheet/ExpectedSchoolAverageReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_greensheet_expectedschoolaveragereport_expectedschoolaveragereportrepository_java")
public class ExpectedSchoolAverageReportRepository extends PageArtifactRepository {
    public ExpectedSchoolAverageReportRepository() {
        super("ExamMaster", "report", "ExpectedSchoolAverageReport", "ExamMaster/Greensheet/ExpectedSchoolAverageReport.jsx");
    }
}