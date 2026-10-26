package com.edufox.schoolerp.uploads.work.work_assignment;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Work/Assignment.jsx. */
@Service("page_uploads_work_work_assignment_assignmentservice_java")
public class AssignmentService extends PageArtifactService<AssignmentRepository> {
    public AssignmentService(AssignmentRepository repository) {
        super(repository);
    }
}