package com.edufox.schoolerp.examination.update.delete_deletemarks;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/delete/DeleteMarks.jsx. */
@Service("page_examination_update_delete_deletemarks_deletemarksservice_java")
public class DeleteMarksService extends PageArtifactService<DeleteMarksRepository> {
    public DeleteMarksService(DeleteMarksRepository repository) {
        super(repository);
    }
}