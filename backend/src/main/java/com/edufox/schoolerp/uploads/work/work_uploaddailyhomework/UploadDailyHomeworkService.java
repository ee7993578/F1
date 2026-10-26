package com.edufox.schoolerp.uploads.work.work_uploaddailyhomework;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Work/UploadDailyHomework.jsx. */
@Service("page_uploads_work_work_uploaddailyhomework_uploaddailyhomeworkservice_java")
public class UploadDailyHomeworkService extends PageArtifactService<UploadDailyHomeworkRepository> {
    public UploadDailyHomeworkService(UploadDailyHomeworkRepository repository) {
        super(repository);
    }
}