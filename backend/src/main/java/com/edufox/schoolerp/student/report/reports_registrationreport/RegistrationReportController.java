package com.edufox.schoolerp.student.report.reports_registrationreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/RegistrationReport.jsx. */
@RestController("page_student_report_reports_registrationreport_registrationreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_registrationreport")
public class RegistrationReportController extends PageArtifactController<RegistrationReportService> {
    public RegistrationReportController(RegistrationReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}