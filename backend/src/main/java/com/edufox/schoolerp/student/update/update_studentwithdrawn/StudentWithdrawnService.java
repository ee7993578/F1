package com.edufox.schoolerp.student.update.update_studentwithdrawn;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/StudentWithdrawn.jsx. */
@Service("page_student_update_update_studentwithdrawn_studentwithdrawnservice_java")
public class StudentWithdrawnService extends PageArtifactService<StudentWithdrawnRepository> {
    public StudentWithdrawnService(StudentWithdrawnRepository repository) {
        super(repository);
    }
}