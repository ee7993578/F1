package com.edufox.schoolerp.student.report.reports_studentinfosheet_copy;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/StudentInfoSheet copy.jsx. */
@RestController("page_student_report_reports_studentinfosheet_copy_studentinfosheet_copycontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_studentinfosheet_copy")
public class StudentInfoSheet_copyController extends PageArtifactController<StudentInfoSheet_copyService> {
    public StudentInfoSheet_copyController(StudentInfoSheet_copyService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}