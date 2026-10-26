package com.edufox.schoolerp.uploads.work.work_uploadholidayhomework;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Work/UploadHolidayHomework.jsx. */
@Service("page_uploads_work_work_uploadholidayhomework_uploadholidayhomeworkservice_java")
public class UploadHolidayHomeworkService extends PageArtifactService<UploadHolidayHomeworkRepository> {
    public UploadHolidayHomeworkService(UploadHolidayHomeworkRepository repository) {
        super(repository);
    }
}