package com.edufox.schoolerp.website.work.work_photogallery;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for WebSite/Work/PhotoGallery.jsx. */
@Service("page_website_work_work_photogallery_photogalleryservice_java")
public class PhotoGalleryService extends PageArtifactService<PhotoGalleryRepository> {
    public PhotoGalleryService(PhotoGalleryRepository repository) {
        super(repository);
    }
}