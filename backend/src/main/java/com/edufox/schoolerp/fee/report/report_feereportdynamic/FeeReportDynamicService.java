package com.edufox.schoolerp.fee.report.report_feereportdynamic;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/FeeReportDynamic.jsx. */
@Service("page_fee_report_report_feereportdynamic_feereportdynamicservice_java")
public class FeeReportDynamicService extends PageArtifactService<FeeReportDynamicRepository> {
    public FeeReportDynamicService(FeeReportDynamicRepository repository) {
        super(repository);
    }
}