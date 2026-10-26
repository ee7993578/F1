package com.edufox.schoolerp.student.report.reports_downloadregistrationform;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/DownloadRegistrationForm.jsx. */
@RestController("page_student_report_reports_downloadregistrationform_downloadregistrationformcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_downloadregistrationform")
public class DownloadRegistrationFormController extends PageArtifactController<DownloadRegistrationFormService> {
    public DownloadRegistrationFormController(DownloadRegistrationFormService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}