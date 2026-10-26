package com.edufox.schoolerp.examination.update.update_deletemarks;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Update/DeleteMarks.jsx. */
@Service("page_examination_update_update_deletemarks_deletemarksservice_java")
public class DeleteMarksService extends PageArtifactService<DeleteMarksRepository> {
    public DeleteMarksService(DeleteMarksRepository repository) {
        super(repository);
    }
}