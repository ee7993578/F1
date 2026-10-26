package com.edufox.schoolerp.uploads.work.work_uploaddailyhomeworkexact;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Work/UploadDailyHomeworkExact.jsx. */
@Service("page_uploads_work_work_uploaddailyhomeworkexact_uploaddailyhomeworkexactservice_java")
public class UploadDailyHomeworkExactService extends PageArtifactService<UploadDailyHomeworkExactRepository> {
    public UploadDailyHomeworkExactService(UploadDailyHomeworkExactRepository repository) {
        super(repository);
    }
}