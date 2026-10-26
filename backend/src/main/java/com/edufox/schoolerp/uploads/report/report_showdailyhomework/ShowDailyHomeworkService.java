package com.edufox.schoolerp.uploads.report.report_showdailyhomework;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Report/ShowDailyHomework.jsx. */
@Service("page_uploads_report_report_showdailyhomework_showdailyhomeworkservice_java")
public class ShowDailyHomeworkService extends PageArtifactService<ShowDailyHomeworkRepository> {
    public ShowDailyHomeworkService(ShowDailyHomeworkRepository repository) {
        super(repository);
    }
}