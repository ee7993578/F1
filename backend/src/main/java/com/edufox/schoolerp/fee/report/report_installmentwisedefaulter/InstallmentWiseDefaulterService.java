package com.edufox.schoolerp.fee.report.report_installmentwisedefaulter;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/InstallmentWiseDefaulter.jsx. */
@Service("page_fee_report_report_installmentwisedefaulter_installmentwisedefaulterservice_java")
public class InstallmentWiseDefaulterService extends PageArtifactService<InstallmentWiseDefaulterRepository> {
    public InstallmentWiseDefaulterService(InstallmentWiseDefaulterRepository repository) {
        super(repository);
    }
}