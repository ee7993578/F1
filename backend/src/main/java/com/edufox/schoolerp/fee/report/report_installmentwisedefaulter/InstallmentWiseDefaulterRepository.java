package com.edufox.schoolerp.fee.report.report_installmentwisedefaulter;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Report/InstallmentWiseDefaulter.jsx. */
@Repository("page_com_edufox_schoolerp_fee_report_report_installmentwisedefaulter_installmentwisedefaulterrepository_java")
public class InstallmentWiseDefaulterRepository extends PageArtifactRepository {
    public InstallmentWiseDefaulterRepository() {
        super("Fee", "report", "InstallmentWiseDefaulter", "Fee/Report/InstallmentWiseDefaulter.jsx");
    }
}