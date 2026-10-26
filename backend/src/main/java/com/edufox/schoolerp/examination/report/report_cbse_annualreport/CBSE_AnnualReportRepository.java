package com.edufox.schoolerp.examination.report.report_cbse_annualreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/CBSE_AnnualReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_cbse_annualreport_cbse_annualreportrepository_java")
public class CBSE_AnnualReportRepository extends PageArtifactRepository {
    public CBSE_AnnualReportRepository() {
        super("ExamMaster", "report", "CBSE_AnnualReport", "ExamMaster/Report/CBSE_AnnualReport.jsx");
    }
}