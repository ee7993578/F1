package com.edufox.schoolerp.library.setup.setup_publishercategory;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Setup/PublisherCategory.jsx. */
@Repository("page_com_edufox_schoolerp_library_setup_setup_publishercategory_publishercategoryrepository_java")
public class PublisherCategoryRepository extends PageArtifactRepository {
    public PublisherCategoryRepository() {
        super("Library", "setup", "PublisherCategory", "Library/Setup/PublisherCategory.jsx");
    }
}