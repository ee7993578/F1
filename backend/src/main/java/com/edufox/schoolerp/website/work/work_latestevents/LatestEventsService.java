package com.edufox.schoolerp.website.work.work_latestevents;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for WebSite/Work/LatestEvents.jsx. */
@Service("page_website_work_work_latestevents_latesteventsservice_java")
public class LatestEventsService extends PageArtifactService<LatestEventsRepository> {
    public LatestEventsService(LatestEventsRepository repository) {
        super(repository);
    }
}