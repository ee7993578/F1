package com.edufox.schoolerp.uploads.work.work_uploadholidayhomework;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Work/UploadHolidayHomework.jsx. */
@RestController("page_uploads_work_work_uploadholidayhomework_uploadholidayhomeworkcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/work/work_uploadholidayhomework")
public class UploadHolidayHomeworkController extends PageArtifactController<UploadHolidayHomeworkService> {
    public UploadHolidayHomeworkController(UploadHolidayHomeworkService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}