package com.edufox.schoolerp.website.work.work_photogallery;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for WebSite/Work/PhotoGallery.jsx. */
@RestController("page_website_work_work_photogallery_photogallerycontroller_java")
@RequestMapping("/api/page-artifacts/website/work/work_photogallery")
public class PhotoGalleryController extends PageArtifactController<PhotoGalleryService> {
    public PhotoGalleryController(PhotoGalleryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}