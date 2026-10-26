package com.edufox.schoolerp.student.update.update_downloadstudentimages;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/DownloadStudentImages.jsx. */
@RestController("page_student_update_update_downloadstudentimages_downloadstudentimagescontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_downloadstudentimages")
public class DownloadStudentImagesController extends PageArtifactController<DownloadStudentImagesService> {
    public DownloadStudentImagesController(DownloadStudentImagesService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}