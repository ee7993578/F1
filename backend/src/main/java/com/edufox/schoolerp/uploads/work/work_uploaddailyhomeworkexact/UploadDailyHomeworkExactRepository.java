package com.edufox.schoolerp.uploads.work.work_uploaddailyhomeworkexact;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Work/UploadDailyHomeworkExact.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_work_work_uploaddailyhomeworkexact_uploaddailyhomeworkexactrepository_java")
public class UploadDailyHomeworkExactRepository extends PageArtifactRepository {
    public UploadDailyHomeworkExactRepository() {
        super("Uploads", "work", "UploadDailyHomeworkExact", "Uploads/Work/UploadDailyHomeworkExact.jsx");
    }
}