package com.edufox.schoolerp.student.update.update_downloadstudentimages;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/DownloadStudentImages.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_downloadstudentimages_downloadstudentimagesrepository_java")
public class DownloadStudentImagesRepository extends PageArtifactRepository {
    public DownloadStudentImagesRepository() {
        super("Student", "update", "DownloadStudentImages", "Student/Update/DownloadStudentImages.jsx");
    }
}