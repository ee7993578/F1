package com.edufox.schoolerp.canteen.report.report_tuckshopproductissuedetail;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Report/TuckshopProductIssueDetail.jsx. */
@RestController("page_canteen_report_report_tuckshopproductissuedetail_tuckshopproductissuedetailcontroller_java")
@RequestMapping("/api/page-artifacts/canteen/report/report_tuckshopproductissuedetail")
public class TuckshopProductIssueDetailController extends PageArtifactController<TuckshopProductIssueDetailService> {
    public TuckshopProductIssueDetailController(TuckshopProductIssueDetailService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}