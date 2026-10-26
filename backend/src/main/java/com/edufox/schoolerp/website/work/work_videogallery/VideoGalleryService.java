package com.edufox.schoolerp.website.work.work_videogallery;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for WebSite/Work/VideoGallery.jsx. */
@Service("page_website_work_work_videogallery_videogalleryservice_java")
public class VideoGalleryService extends PageArtifactService<VideoGalleryRepository> {
    public VideoGalleryService(VideoGalleryRepository repository) {
        super(repository);
    }
}