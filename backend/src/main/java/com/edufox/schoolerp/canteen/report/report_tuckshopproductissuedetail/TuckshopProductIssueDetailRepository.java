package com.edufox.schoolerp.canteen.report.report_tuckshopproductissuedetail;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Report/TuckshopProductIssueDetail.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_report_report_tuckshopproductissuedetail_tuckshopproductissuedetailrepository_java")
public class TuckshopProductIssueDetailRepository extends PageArtifactRepository {
    public TuckshopProductIssueDetailRepository() {
        super("Canteen", "report", "TuckshopProductIssueDetail", "Canteen/Report/TuckshopProductIssueDetail.jsx");
    }
}