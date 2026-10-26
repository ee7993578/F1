package com.edufox.schoolerp.uploads.work.work_uploaddailyhomeworkexact;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Work/UploadDailyHomeworkExact.jsx. */
@RestController("page_uploads_work_work_uploaddailyhomeworkexact_uploaddailyhomeworkexactcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/work/work_uploaddailyhomeworkexact")
public class UploadDailyHomeworkExactController extends PageArtifactController<UploadDailyHomeworkExactService> {
    public UploadDailyHomeworkExactController(UploadDailyHomeworkExactService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}