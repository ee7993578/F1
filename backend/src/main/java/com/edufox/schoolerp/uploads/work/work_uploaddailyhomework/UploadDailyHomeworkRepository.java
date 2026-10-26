package com.edufox.schoolerp.uploads.work.work_uploaddailyhomework;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Work/UploadDailyHomework.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_work_work_uploaddailyhomework_uploaddailyhomeworkrepository_java")
public class UploadDailyHomeworkRepository extends PageArtifactRepository {
    public UploadDailyHomeworkRepository() {
        super("Uploads", "work", "UploadDailyHomework", "Uploads/Work/UploadDailyHomework.jsx");
    }
}