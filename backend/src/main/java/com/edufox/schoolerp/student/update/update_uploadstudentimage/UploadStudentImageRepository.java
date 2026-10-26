package com.edufox.schoolerp.student.update.update_uploadstudentimage;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/UploadStudentImage.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_uploadstudentimage_uploadstudentimagerepository_java")
public class UploadStudentImageRepository extends PageArtifactRepository {
    public UploadStudentImageRepository() {
        super("Student", "update", "UploadStudentImage", "Student/Update/UploadStudentImage.jsx");
    }
}