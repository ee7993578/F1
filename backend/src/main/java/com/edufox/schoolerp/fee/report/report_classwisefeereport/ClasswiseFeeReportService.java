package com.edufox.schoolerp.fee.report.report_classwisefeereport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/ClasswiseFeeReport.jsx. */
@Service("page_fee_report_report_classwisefeereport_classwisefeereportservice_java")
public class ClasswiseFeeReportService extends PageArtifactService<ClasswiseFeeReportRepository> {
    public ClasswiseFeeReportService(ClasswiseFeeReportRepository repository) {
        super(repository);
    }
}