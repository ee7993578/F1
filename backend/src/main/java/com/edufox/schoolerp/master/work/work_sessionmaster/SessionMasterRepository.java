package com.edufox.schoolerp.master.work.work_sessionmaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/SessionMaster.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_sessionmaster_sessionmasterrepository_java")
public class SessionMasterRepository extends PageArtifactRepository {
    public SessionMasterRepository() {
        super("MasterSetting", "work", "SessionMaster", "MasterSetting/Work/SessionMaster.jsx");
    }
}