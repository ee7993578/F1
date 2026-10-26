package com.edufox.schoolerp.uploads.work.work_uploadsyllabus;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Work/UploadSyllabus.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_work_work_uploadsyllabus_uploadsyllabusrepository_java")
public class UploadSyllabusRepository extends PageArtifactRepository {
    public UploadSyllabusRepository() {
        super("Uploads", "work", "UploadSyllabus", "Uploads/Work/UploadSyllabus.jsx");
    }
}