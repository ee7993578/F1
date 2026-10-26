package com.edufox.schoolerp.website.work.work_uploadnotice;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for WebSite/Work/UploadNotice.jsx. */
@RestController("page_website_work_work_uploadnotice_uploadnoticecontroller_java")
@RequestMapping("/api/page-artifacts/website/work/work_uploadnotice")
public class UploadNoticeController extends PageArtifactController<UploadNoticeService> {
    public UploadNoticeController(UploadNoticeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}