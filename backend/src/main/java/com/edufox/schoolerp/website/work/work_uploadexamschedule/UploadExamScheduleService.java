package com.edufox.schoolerp.website.work.work_uploadexamschedule;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for WebSite/Work/UploadExamSchedule.jsx. */
@Service("page_website_work_work_uploadexamschedule_uploadexamscheduleservice_java")
public class UploadExamScheduleService extends PageArtifactService<UploadExamScheduleRepository> {
    public UploadExamScheduleService(UploadExamScheduleRepository repository) {
        super(repository);
    }
}