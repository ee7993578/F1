package com.edufox.schoolerp.master.work.work_session_term;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/Session_term.jsx. */
@Service("page_master_work_work_session_term_session_termservice_java")
public class Session_termService extends PageArtifactService<Session_termRepository> {
    public Session_termService(Session_termRepository repository) {
        super(repository);
    }
}