package com.edufox.schoolerp.examination.work.marksentry_uploadmarks;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marksEntry/UploadMarks.jsx. */
@Service("page_examination_work_marksentry_uploadmarks_uploadmarksservice_java")
public class UploadMarksService extends PageArtifactService<UploadMarksRepository> {
    public UploadMarksService(UploadMarksRepository repository) {
        super(repository);
    }
}