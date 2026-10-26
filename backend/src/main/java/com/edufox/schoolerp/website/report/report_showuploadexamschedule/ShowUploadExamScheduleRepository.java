package com.edufox.schoolerp.website.report.report_showuploadexamschedule;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for WebSite/Report/ShowUploadExamSchedule.jsx. */
@Repository("page_com_edufox_schoolerp_website_report_report_showuploadexamschedule_showuploadexamschedulerepository_java")
public class ShowUploadExamScheduleRepository extends PageArtifactRepository {
    public ShowUploadExamScheduleRepository() {
        super("WebSite", "report", "ShowUploadExamSchedule", "WebSite/Report/ShowUploadExamSchedule.jsx");
    }
}