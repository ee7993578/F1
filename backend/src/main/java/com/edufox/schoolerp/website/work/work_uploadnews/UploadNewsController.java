package com.edufox.schoolerp.website.work.work_uploadnews;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for WebSite/Work/UploadNews.jsx. */
@RestController("page_website_work_work_uploadnews_uploadnewscontroller_java")
@RequestMapping("/api/page-artifacts/website/work/work_uploadnews")
public class UploadNewsController extends PageArtifactController<UploadNewsService> {
    public UploadNewsController(UploadNewsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}