package com.edufox.schoolerp.examination.work.marksentry_stuhealthstatus;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marksEntry/StuHealthStatus.jsx. */
@Service("page_examination_work_marksentry_stuhealthstatus_stuhealthstatusservice_java")
public class StuHealthStatusService extends PageArtifactService<StuHealthStatusRepository> {
    public StuHealthStatusService(StuHealthStatusRepository repository) {
        super(repository);
    }
}