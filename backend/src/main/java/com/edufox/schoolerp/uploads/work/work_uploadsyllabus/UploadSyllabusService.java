package com.edufox.schoolerp.uploads.work.work_uploadsyllabus;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Work/UploadSyllabus.jsx. */
@Service("page_uploads_work_work_uploadsyllabus_uploadsyllabusservice_java")
public class UploadSyllabusService extends PageArtifactService<UploadSyllabusRepository> {
    public UploadSyllabusService(UploadSyllabusRepository repository) {
        super(repository);
    }
}