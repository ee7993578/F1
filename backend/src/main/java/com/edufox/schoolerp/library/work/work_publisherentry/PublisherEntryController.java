package com.edufox.schoolerp.library.work.work_publisherentry;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Work/PublisherEntry.jsx. */
@RestController("page_library_work_work_publisherentry_publisherentrycontroller_java")
@RequestMapping("/api/page-artifacts/library/work/work_publisherentry")
public class PublisherEntryController extends PageArtifactController<PublisherEntryService> {
    public PublisherEntryController(PublisherEntryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}