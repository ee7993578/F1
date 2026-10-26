package com.edufox.schoolerp.fee.work.work_latefeereportwork;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/LateFeeReportWork.jsx. */
@RestController("page_fee_work_work_latefeereportwork_latefeereportworkcontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_latefeereportwork")
public class LateFeeReportWorkController extends PageArtifactController<LateFeeReportWorkService> {
    public LateFeeReportWorkController(LateFeeReportWorkService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}