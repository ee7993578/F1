package com.edufox.schoolerp.examination.update.delete_deletemarks;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/delete/DeleteMarks.jsx. */
@Repository("page_com_edufox_schoolerp_examination_update_delete_deletemarks_deletemarksrepository_java")
public class DeleteMarksRepository extends PageArtifactRepository {
    public DeleteMarksRepository() {
        super("ExamMaster", "update", "DeleteMarks", "ExamMaster/delete/DeleteMarks.jsx");
    }
}