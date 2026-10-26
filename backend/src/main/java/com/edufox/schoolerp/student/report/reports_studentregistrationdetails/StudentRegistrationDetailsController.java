package com.edufox.schoolerp.student.report.reports_studentregistrationdetails;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/StudentRegistrationDetails.jsx. */
@RestController("page_student_report_reports_studentregistrationdetails_studentregistrationdetailscontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_studentregistrationdetails")
public class StudentRegistrationDetailsController extends PageArtifactController<StudentRegistrationDetailsService> {
    public StudentRegistrationDetailsController(StudentRegistrationDetailsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}