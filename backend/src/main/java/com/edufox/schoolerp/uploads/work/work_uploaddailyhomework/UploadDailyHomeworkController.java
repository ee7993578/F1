package com.edufox.schoolerp.uploads.work.work_uploaddailyhomework;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Work/UploadDailyHomework.jsx. */
@RestController("page_uploads_work_work_uploaddailyhomework_uploaddailyhomeworkcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/work/work_uploaddailyhomework")
public class UploadDailyHomeworkController extends PageArtifactController<UploadDailyHomeworkService> {
    public UploadDailyHomeworkController(UploadDailyHomeworkService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}