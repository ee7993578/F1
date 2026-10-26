package com.edufox.schoolerp.examination.work.work_stuhealthstatus;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/StuHealthStatus.jsx. */
@Service("page_examination_work_work_stuhealthstatus_stuhealthstatusservice_java")
public class StuHealthStatusService extends PageArtifactService<StuHealthStatusRepository> {
    public StuHealthStatusService(StuHealthStatusRepository repository) {
        super(repository);
    }
}