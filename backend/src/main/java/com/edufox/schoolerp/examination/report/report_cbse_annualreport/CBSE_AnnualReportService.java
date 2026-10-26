package com.edufox.schoolerp.examination.report.report_cbse_annualreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/CBSE_AnnualReport.jsx. */
@Service("page_examination_report_report_cbse_annualreport_cbse_annualreportservice_java")
public class CBSE_AnnualReportService extends PageArtifactService<CBSE_AnnualReportRepository> {
    public CBSE_AnnualReportService(CBSE_AnnualReportRepository repository) {
        super(repository);
    }
}