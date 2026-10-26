package com.edufox.schoolerp.student.report.reports_onlineregistrationreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/OnlineRegistrationReport.jsx. */
@RestController("page_student_report_reports_onlineregistrationreport_onlineregistrationreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_onlineregistrationreport")
public class OnlineRegistrationReportController extends PageArtifactController<OnlineRegistrationReportService> {
    public OnlineRegistrationReportController(OnlineRegistrationReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}