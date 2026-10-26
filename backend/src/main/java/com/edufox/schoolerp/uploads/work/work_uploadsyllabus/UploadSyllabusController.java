package com.edufox.schoolerp.uploads.work.work_uploadsyllabus;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Work/UploadSyllabus.jsx. */
@RestController("page_uploads_work_work_uploadsyllabus_uploadsyllabuscontroller_java")
@RequestMapping("/api/page-artifacts/uploads/work/work_uploadsyllabus")
public class UploadSyllabusController extends PageArtifactController<UploadSyllabusService> {
    public UploadSyllabusController(UploadSyllabusService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}