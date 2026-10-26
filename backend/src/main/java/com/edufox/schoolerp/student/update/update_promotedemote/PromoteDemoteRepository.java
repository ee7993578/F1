package com.edufox.schoolerp.student.update.update_promotedemote;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/PromoteDemote.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_promotedemote_promotedemoterepository_java")
public class PromoteDemoteRepository extends PageArtifactRepository {
    public PromoteDemoteRepository() {
        super("Student", "update", "PromoteDemote", "Student/Update/PromoteDemote.jsx");
    }
}