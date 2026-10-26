package com.edufox.schoolerp.website.work.work_videogallery;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for WebSite/Work/VideoGallery.jsx. */
@Repository("page_com_edufox_schoolerp_website_work_work_videogallery_videogalleryrepository_java")
public class VideoGalleryRepository extends PageArtifactRepository {
    public VideoGalleryRepository() {
        super("WebSite", "work", "VideoGallery", "WebSite/Work/VideoGallery.jsx");
    }
}