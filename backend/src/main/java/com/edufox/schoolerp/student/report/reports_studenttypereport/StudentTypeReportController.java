package com.edufox.schoolerp.student.report.reports_studenttypereport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/StudentTypeReport.jsx. */
@RestController("page_student_report_reports_studenttypereport_studenttypereportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_studenttypereport")
public class StudentTypeReportController extends PageArtifactController<StudentTypeReportService> {
    public StudentTypeReportController(StudentTypeReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}