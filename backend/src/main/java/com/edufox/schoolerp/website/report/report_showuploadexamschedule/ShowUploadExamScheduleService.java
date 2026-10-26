package com.edufox.schoolerp.website.report.report_showuploadexamschedule;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for WebSite/Report/ShowUploadExamSchedule.jsx. */
@Service("page_website_report_report_showuploadexamschedule_showuploadexamscheduleservice_java")
public class ShowUploadExamScheduleService extends PageArtifactService<ShowUploadExamScheduleRepository> {
    public ShowUploadExamScheduleService(ShowUploadExamScheduleRepository repository) {
        super(repository);
    }
}