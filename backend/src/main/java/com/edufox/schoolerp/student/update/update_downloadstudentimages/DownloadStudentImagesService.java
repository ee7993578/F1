package com.edufox.schoolerp.student.update.update_downloadstudentimages;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/DownloadStudentImages.jsx. */
@Service("page_student_update_update_downloadstudentimages_downloadstudentimagesservice_java")
public class DownloadStudentImagesService extends PageArtifactService<DownloadStudentImagesRepository> {
    public DownloadStudentImagesService(DownloadStudentImagesRepository repository) {
        super(repository);
    }
}