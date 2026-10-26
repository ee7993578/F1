package com.edufox.schoolerp.website.work.work_videogallery;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for WebSite/Work/VideoGallery.jsx. */
@RestController("page_website_work_work_videogallery_videogallerycontroller_java")
@RequestMapping("/api/page-artifacts/website/work/work_videogallery")
public class VideoGalleryController extends PageArtifactController<VideoGalleryService> {
    public VideoGalleryController(VideoGalleryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}