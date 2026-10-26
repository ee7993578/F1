package com.edufox.schoolerp.student.update.update_uploadstudentimage;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/UploadStudentImage.jsx. */
@RestController("page_student_update_update_uploadstudentimage_uploadstudentimagecontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_uploadstudentimage")
public class UploadStudentImageController extends PageArtifactController<UploadStudentImageService> {
    public UploadStudentImageController(UploadStudentImageService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}