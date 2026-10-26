package com.edufox.schoolerp.library.report.report_renewdetails;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Report/RenewDetails.jsx. */
@Service("page_library_report_report_renewdetails_renewdetailsservice_java")
public class RenewDetailsService extends PageArtifactService<RenewDetailsRepository> {
    public RenewDetailsService(RenewDetailsRepository repository) {
        super(repository);
    }
}