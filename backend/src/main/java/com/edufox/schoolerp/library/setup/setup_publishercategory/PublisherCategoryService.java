package com.edufox.schoolerp.library.setup.setup_publishercategory;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Setup/PublisherCategory.jsx. */
@Service("page_library_setup_setup_publishercategory_publishercategoryservice_java")
public class PublisherCategoryService extends PageArtifactService<PublisherCategoryRepository> {
    public PublisherCategoryService(PublisherCategoryRepository repository) {
        super(repository);
    }
}