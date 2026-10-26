package com.edufox.schoolerp.master.work.work_session_term;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/Session_term.jsx. */
@RestController("page_master_work_work_session_term_session_termcontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_session_term")
public class Session_termController extends PageArtifactController<Session_termService> {
    public Session_termController(Session_termService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}