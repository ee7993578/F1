package com.edufox.schoolerp.student.report.reports_newstudentreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/NewStudentReport.jsx. */
@RestController("page_student_report_reports_newstudentreport_newstudentreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_newstudentreport")
public class NewStudentReportController extends PageArtifactController<NewStudentReportService> {
    public NewStudentReportController(NewStudentReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}