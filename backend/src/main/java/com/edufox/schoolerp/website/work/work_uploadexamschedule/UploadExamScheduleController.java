package com.edufox.schoolerp.website.work.work_uploadexamschedule;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for WebSite/Work/UploadExamSchedule.jsx. */
@RestController("page_website_work_work_uploadexamschedule_uploadexamschedulecontroller_java")
@RequestMapping("/api/page-artifacts/website/work/work_uploadexamschedule")
public class UploadExamScheduleController extends PageArtifactController<UploadExamScheduleService> {
    public UploadExamScheduleController(UploadExamScheduleService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}