package com.edufox.schoolerp.library.report.report_renewdetails;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Report/RenewDetails.jsx. */
@Repository("page_com_edufox_schoolerp_library_report_report_renewdetails_renewdetailsrepository_java")
public class RenewDetailsRepository extends PageArtifactRepository {
    public RenewDetailsRepository() {
        super("Library", "report", "RenewDetails", "Library/Report/RenewDetails.jsx");
    }
}