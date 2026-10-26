package com.edufox.schoolerp.website.work.work_uploadtc;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for WebSite/Work/UploadTC.jsx. */
@RestController("page_website_work_work_uploadtc_uploadtccontroller_java")
@RequestMapping("/api/page-artifacts/website/work/work_uploadtc")
public class UploadTCController extends PageArtifactController<UploadTCService> {
    public UploadTCController(UploadTCService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}