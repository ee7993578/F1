package com.edufox.schoolerp.student.update.update_changeadmno;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/ChangeAdmNo.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_changeadmno_changeadmnorepository_java")
public class ChangeAdmNoRepository extends PageArtifactRepository {
    public ChangeAdmNoRepository() {
        super("Student", "update", "ChangeAdmNo", "Student/Update/ChangeAdmNo.jsx");
    }
}