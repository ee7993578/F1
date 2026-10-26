package com.edufox.schoolerp.website.work.work_latestevents;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for WebSite/Work/LatestEvents.jsx. */
@RestController("page_website_work_work_latestevents_latesteventscontroller_java")
@RequestMapping("/api/page-artifacts/website/work/work_latestevents")
public class LatestEventsController extends PageArtifactController<LatestEventsService> {
    public LatestEventsController(LatestEventsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}