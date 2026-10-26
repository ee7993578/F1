package com.edufox.schoolerp.student.report.reports_admittedstudentreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/AdmittedStudentReport.jsx. */
@RestController("page_student_report_reports_admittedstudentreport_admittedstudentreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_admittedstudentreport")
public class AdmittedStudentReportController extends PageArtifactController<AdmittedStudentReportService> {
    public AdmittedStudentReportController(AdmittedStudentReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}