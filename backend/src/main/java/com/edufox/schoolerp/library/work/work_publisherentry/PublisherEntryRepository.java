package com.edufox.schoolerp.library.work.work_publisherentry;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Work/PublisherEntry.jsx. */
@Repository("page_com_edufox_schoolerp_library_work_work_publisherentry_publisherentryrepository_java")
public class PublisherEntryRepository extends PageArtifactRepository {
    public PublisherEntryRepository() {
        super("Library", "work", "PublisherEntry", "Library/Work/PublisherEntry.jsx");
    }
}