package com.edufox.schoolerp.master.work.work_sessionmaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/SessionMaster.jsx. */
@Service("page_master_work_work_sessionmaster_sessionmasterservice_java")
public class SessionMasterService extends PageArtifactService<SessionMasterRepository> {
    public SessionMasterService(SessionMasterRepository repository) {
        super(repository);
    }
}