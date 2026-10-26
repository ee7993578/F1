package com.edufox.schoolerp.master.work.work_switchsession;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/SwitchSession.jsx. */
@Service("page_master_work_work_switchsession_switchsessionservice_java")
public class SwitchSessionService extends PageArtifactService<SwitchSessionRepository> {
    public SwitchSessionService(SwitchSessionRepository repository) {
        super(repository);
    }
}