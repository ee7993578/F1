package com.edufox.schoolerp.canteen.report.report_tuckshopproductissuedetail;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Report/TuckshopProductIssueDetail.jsx. */
@Service("page_canteen_report_report_tuckshopproductissuedetail_tuckshopproductissuedetailservice_java")
public class TuckshopProductIssueDetailService extends PageArtifactService<TuckshopProductIssueDetailRepository> {
    public TuckshopProductIssueDetailService(TuckshopProductIssueDetailRepository repository) {
        super(repository);
    }
}