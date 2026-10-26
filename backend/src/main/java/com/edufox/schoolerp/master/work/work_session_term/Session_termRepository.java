package com.edufox.schoolerp.master.work.work_session_term;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/Session_term.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_session_term_session_termrepository_java")
public class Session_termRepository extends PageArtifactRepository {
    public Session_termRepository() {
        super("MasterSetting", "work", "Session_term", "MasterSetting/Work/Session_term.jsx");
    }
}