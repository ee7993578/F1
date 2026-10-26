package com.edufox.schoolerp.uploads.report.report_showdailyhomework;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Report/ShowDailyHomework.jsx. */
@RestController("page_uploads_report_report_showdailyhomework_showdailyhomeworkcontroller_java")
@RequestMapping("/api/page-artifacts/uploads/report/report_showdailyhomework")
public class ShowDailyHomeworkController extends PageArtifactController<ShowDailyHomeworkService> {
    public ShowDailyHomeworkController(ShowDailyHomeworkService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}