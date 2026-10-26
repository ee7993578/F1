package com.edufox.schoolerp.student.update.update_studentwithdrawn;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/StudentWithdrawn.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_studentwithdrawn_studentwithdrawnrepository_java")
public class StudentWithdrawnRepository extends PageArtifactRepository {
    public StudentWithdrawnRepository() {
        super("Student", "update", "StudentWithdrawn", "Student/Update/StudentWithdrawn.jsx");
    }
}