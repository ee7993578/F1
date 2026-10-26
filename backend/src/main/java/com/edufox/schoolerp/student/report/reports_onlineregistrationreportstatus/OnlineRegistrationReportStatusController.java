package com.edufox.schoolerp.student.report.reports_onlineregistrationreportstatus;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/OnlineRegistrationReportStatus.jsx. */
@RestController("page_student_report_reports_onlineregistrationreportstatus_onlineregistrationreportstatuscontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_onlineregistrationreportstatus")
public class OnlineRegistrationReportStatusController extends PageArtifactController<OnlineRegistrationReportStatusService> {
    public OnlineRegistrationReportStatusController(OnlineRegistrationReportStatusService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}