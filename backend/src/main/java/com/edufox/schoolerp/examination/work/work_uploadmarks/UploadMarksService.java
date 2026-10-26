package com.edufox.schoolerp.examination.work.work_uploadmarks;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/UploadMarks.jsx. */
@Service("page_examination_work_work_uploadmarks_uploadmarksservice_java")
public class UploadMarksService extends PageArtifactService<UploadMarksRepository> {
    public UploadMarksService(UploadMarksRepository repository) {
        super(repository);
    }
}