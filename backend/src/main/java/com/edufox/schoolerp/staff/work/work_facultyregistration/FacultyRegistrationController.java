package com.edufox.schoolerp.staff.work.work_facultyregistration;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Work/FacultyRegistration.jsx. */
@RestController("page_staff_work_work_facultyregistration_facultyregistrationcontroller_java")
@RequestMapping("/api/page-artifacts/staff/work/work_facultyregistration")
public class FacultyRegistrationController extends PageArtifactController<FacultyRegistrationService> {
    public FacultyRegistrationController(FacultyRegistrationService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}