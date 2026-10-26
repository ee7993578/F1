package com.edufox.schoolerp.student.report.reports_birthdayreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/BirthdayReport.jsx. */
@RestController("page_student_report_reports_birthdayreport_birthdayreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_birthdayreport")
public class BirthdayReportController extends PageArtifactController<BirthdayReportService> {
    public BirthdayReportController(BirthdayReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}