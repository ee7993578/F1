package com.edufox.schoolerp.website.work.work_photogallery;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for WebSite/Work/PhotoGallery.jsx. */
@Repository("page_com_edufox_schoolerp_website_work_work_photogallery_photogalleryrepository_java")
public class PhotoGalleryRepository extends PageArtifactRepository {
    public PhotoGalleryRepository() {
        super("WebSite", "work", "PhotoGallery", "WebSite/Work/PhotoGallery.jsx");
    }
}