package com.edufox.schoolerp.examination.work.work_uploadmarksexact;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/UploadMarksExact.jsx. */
@Service("page_examination_work_work_uploadmarksexact_uploadmarksexactservice_java")
public class UploadMarksExactService extends PageArtifactService<UploadMarksExactRepository> {
    public UploadMarksExactService(UploadMarksExactRepository repository) {
        super(repository);
    }
}