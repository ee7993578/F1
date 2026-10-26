package com.edufox.schoolerp.master.work.work_switchsession;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/SwitchSession.jsx. */
@RestController("page_master_work_work_switchsession_switchsessioncontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_switchsession")
public class SwitchSessionController extends PageArtifactController<SwitchSessionService> {
    public SwitchSessionController(SwitchSessionService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}