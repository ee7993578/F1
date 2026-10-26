package com.edufox.schoolerp.student.update.update_uploadstudentimage;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/UploadStudentImage.jsx. */
@Service("page_student_update_update_uploadstudentimage_uploadstudentimageservice_java")
public class UploadStudentImageService extends PageArtifactService<UploadStudentImageRepository> {
    public UploadStudentImageService(UploadStudentImageRepository repository) {
        super(repository);
    }
}