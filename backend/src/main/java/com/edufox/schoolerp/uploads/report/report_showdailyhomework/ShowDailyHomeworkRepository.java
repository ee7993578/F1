package com.edufox.schoolerp.uploads.report.report_showdailyhomework;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Report/ShowDailyHomework.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_report_report_showdailyhomework_showdailyhomeworkrepository_java")
public class ShowDailyHomeworkRepository extends PageArtifactRepository {
    public ShowDailyHomeworkRepository() {
        super("Uploads", "report", "ShowDailyHomework", "Uploads/Report/ShowDailyHomework.jsx");
    }
}