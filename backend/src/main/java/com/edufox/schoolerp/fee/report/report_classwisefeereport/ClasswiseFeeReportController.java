package com.edufox.schoolerp.fee.report.report_classwisefeereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/ClasswiseFeeReport.jsx. */
@RestController("page_fee_report_report_classwisefeereport_classwisefeereportcontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_classwisefeereport")
public class ClasswiseFeeReportController extends PageArtifactController<ClasswiseFeeReportService> {
    public ClasswiseFeeReportController(ClasswiseFeeReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}