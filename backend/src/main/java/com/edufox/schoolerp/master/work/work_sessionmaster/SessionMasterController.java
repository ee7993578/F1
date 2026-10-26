package com.edufox.schoolerp.master.work.work_sessionmaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/SessionMaster.jsx. */
@RestController("page_master_work_work_sessionmaster_sessionmastercontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_sessionmaster")
public class SessionMasterController extends PageArtifactController<SessionMasterService> {
    public SessionMasterController(SessionMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}