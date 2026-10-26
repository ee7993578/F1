package com.edufox.schoolerp.staff.work.work_facultyreregistration;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Work/FacultyReregistration.jsx. */
@RestController("page_staff_work_work_facultyreregistration_facultyreregistrationcontroller_java")
@RequestMapping("/api/page-artifacts/staff/work/work_facultyreregistration")
public class FacultyReregistrationController extends PageArtifactController<FacultyReregistrationService> {
    public FacultyReregistrationController(FacultyReregistrationService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}