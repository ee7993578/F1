package com.edufox.schoolerp.fee.report.report_studentfeesummary;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Report/StudentFeeSummary.jsx. */
@RestController("page_fee_report_report_studentfeesummary_studentfeesummarycontroller_java")
@RequestMapping("/api/page-artifacts/fee/report/report_studentfeesummary")
public class StudentFeeSummaryController extends PageArtifactController<StudentFeeSummaryService> {
    public StudentFeeSummaryController(StudentFeeSummaryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}