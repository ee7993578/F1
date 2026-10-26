package com.edufox.schoolerp.student.report.reports_studentidpassword;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/StudentIDPassword.jsx. */
@RestController("page_student_report_reports_studentidpassword_studentidpasswordcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_studentidpassword")
public class StudentIDPasswordController extends PageArtifactController<StudentIDPasswordService> {
    public StudentIDPasswordController(StudentIDPasswordService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}