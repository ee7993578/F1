package com.edufox.schoolerp.staff.report.report_classteacherreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Report/ClassTeacherReport.jsx. */
@RestController("page_staff_report_report_classteacherreport_classteacherreportcontroller_java")
@RequestMapping("/api/page-artifacts/staff/report/report_classteacherreport")
public class ClassTeacherReportController extends PageArtifactController<ClassTeacherReportService> {
    public ClassTeacherReportController(ClassTeacherReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}