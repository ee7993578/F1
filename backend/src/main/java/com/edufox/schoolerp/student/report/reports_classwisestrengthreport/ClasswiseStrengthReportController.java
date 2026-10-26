package com.edufox.schoolerp.student.report.reports_classwisestrengthreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/ClasswiseStrengthReport.jsx. */
@RestController("page_student_report_reports_classwisestrengthreport_classwisestrengthreportcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_classwisestrengthreport")
public class ClasswiseStrengthReportController extends PageArtifactController<ClasswiseStrengthReportService> {
    public ClasswiseStrengthReportController(ClasswiseStrengthReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}