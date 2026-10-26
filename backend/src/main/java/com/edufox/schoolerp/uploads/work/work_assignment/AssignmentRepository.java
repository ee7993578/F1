package com.edufox.schoolerp.uploads.work.work_assignment;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Work/Assignment.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_work_work_assignment_assignmentrepository_java")
public class AssignmentRepository extends PageArtifactRepository {
    public AssignmentRepository() {
        super("Uploads", "work", "Assignment", "Uploads/Work/Assignment.jsx");
    }
}