package com.edufox.schoolerp.library.work.work_publisherentry;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Work/PublisherEntry.jsx. */
@Service("page_library_work_work_publisherentry_publisherentryservice_java")
public class PublisherEntryService extends PageArtifactService<PublisherEntryRepository> {
    public PublisherEntryService(PublisherEntryRepository repository) {
        super(repository);
    }
}