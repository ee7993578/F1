package com.edufox.schoolerp.canteen.report.report_tuckshopreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Report/TuckshopReport.jsx. */
@RestController("page_canteen_report_report_tuckshopreport_tuckshopreportcontroller_java")
@RequestMapping("/api/page-artifacts/canteen/report/report_tuckshopreport")
public class TuckshopReportController extends PageArtifactController<TuckshopReportService> {
    public TuckshopReportController(TuckshopReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}