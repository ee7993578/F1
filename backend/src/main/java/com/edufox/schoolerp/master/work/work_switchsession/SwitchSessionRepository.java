package com.edufox.schoolerp.master.work.work_switchsession;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/SwitchSession.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_switchsession_switchsessionrepository_java")
public class SwitchSessionRepository extends PageArtifactRepository {
    public SwitchSessionRepository() {
        super("MasterSetting", "work", "SwitchSession", "MasterSetting/Work/SwitchSession.jsx");
    }
}