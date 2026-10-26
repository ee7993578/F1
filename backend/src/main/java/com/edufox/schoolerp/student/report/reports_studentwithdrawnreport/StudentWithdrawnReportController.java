package com.edufox.schoolerp.student.report.reports_studentwithdrawnreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/StudentWithdrawnReport.jsx. */
@RestController("page_student_report_reports_studentwithdrawnreport_studentwithdrawnreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_studentwithdrawnreport")
public class StudentWithdrawnReportController extends PageArtifactController<StudentWithdrawnReportService> {
    public StudentWithdrawnReportController(StudentWithdrawnReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}