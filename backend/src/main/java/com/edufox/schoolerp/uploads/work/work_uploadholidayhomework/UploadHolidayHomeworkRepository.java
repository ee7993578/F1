package com.edufox.schoolerp.uploads.work.work_uploadholidayhomework;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Work/UploadHolidayHomework.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_work_work_uploadholidayhomework_uploadholidayhomeworkrepository_java")
public class UploadHolidayHomeworkRepository extends PageArtifactRepository {
    public UploadHolidayHomeworkRepository() {
        super("Uploads", "work", "UploadHolidayHomework", "Uploads/Work/UploadHolidayHomework.jsx");
    }
}