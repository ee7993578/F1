package com.edufox.schoolerp.staff.report.report_jobapplicationreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Report/JobApplicationReport.jsx. */
@RestController("page_staff_report_report_jobapplicationreport_jobapplicationreportcontroller_java")
@RequestMapping("/api/page-artifacts/staff/report/report_jobapplicationreport")
public class JobApplicationReportController extends PageArtifactController<JobApplicationReportService> {
    public JobApplicationReportController(JobApplicationReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}