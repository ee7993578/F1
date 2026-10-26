package com.edufox.schoolerp.fee.work.work_latefeereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/LateFeeReport.jsx. */
@RestController("page_fee_work_work_latefeereport_latefeereportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_latefeereport")
public class LateFeeReportController extends PageArtifactController<LateFeeReportService> {
    public LateFeeReportController(LateFeeReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}