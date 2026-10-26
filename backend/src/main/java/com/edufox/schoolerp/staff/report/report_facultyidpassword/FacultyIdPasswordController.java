package com.edufox.schoolerp.staff.report.report_facultyidpassword;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Report/FacultyIdPassword.jsx. */
@RestController("page_staff_report_report_facultyidpassword_facultyidpasswordcontroller_java")
@RequestMapping("/api/page-artifacts/staff/report/report_facultyidpassword")
public class FacultyIdPasswordController extends PageArtifactController<FacultyIdPasswordService> {
    public FacultyIdPasswordController(FacultyIdPasswordService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}