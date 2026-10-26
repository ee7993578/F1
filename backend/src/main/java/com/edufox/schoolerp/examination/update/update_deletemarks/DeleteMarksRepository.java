package com.edufox.schoolerp.examination.update.update_deletemarks;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Update/DeleteMarks.jsx. */
@Repository("page_com_edufox_schoolerp_examination_update_update_deletemarks_deletemarksrepository_java")
public class DeleteMarksRepository extends PageArtifactRepository {
    public DeleteMarksRepository() {
        super("ExamMaster", "update", "DeleteMarks", "ExamMaster/Update/DeleteMarks.jsx");
    }
}