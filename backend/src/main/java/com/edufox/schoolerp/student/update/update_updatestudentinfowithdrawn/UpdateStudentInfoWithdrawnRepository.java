package com.edufox.schoolerp.student.update.update_updatestudentinfowithdrawn;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/UpdateStudentInfoWithdrawn.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_updatestudentinfowithdrawn_updatestudentinfowithdrawnrepository_java")
public class UpdateStudentInfoWithdrawnRepository extends PageArtifactRepository {
    public UpdateStudentInfoWithdrawnRepository() {
        super("Student", "update", "UpdateStudentInfoWithdrawn", "Student/Update/UpdateStudentInfoWithdrawn.jsx");
    }
}