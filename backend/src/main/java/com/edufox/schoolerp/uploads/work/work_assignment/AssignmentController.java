package com.edufox.schoolerp.uploads.work.work_assignment;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Work/Assignment.jsx. */
@RestController("page_uploads_work_work_assignment_assignmentcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/work/work_assignment")
public class AssignmentController extends PageArtifactController<AssignmentService> {
    public AssignmentController(AssignmentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}