package com.edufox.schoolerp.website.work.work_uploadexamschedule;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for WebSite/Work/UploadExamSchedule.jsx. */
@Repository("page_com_edufox_schoolerp_website_work_work_uploadexamschedule_uploadexamschedulerepository_java")
public class UploadExamScheduleRepository extends PageArtifactRepository {
    public UploadExamScheduleRepository() {
        super("WebSite", "work", "UploadExamSchedule", "WebSite/Work/UploadExamSchedule.jsx");
    }
}