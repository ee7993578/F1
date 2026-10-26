package com.edufox.schoolerp.website.work.work_latestevents;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for WebSite/Work/LatestEvents.jsx. */
@Repository("page_com_edufox_schoolerp_website_work_work_latestevents_latesteventsrepository_java")
public class LatestEventsRepository extends PageArtifactRepository {
    public LatestEventsRepository() {
        super("WebSite", "work", "LatestEvents", "WebSite/Work/LatestEvents.jsx");
    }
}