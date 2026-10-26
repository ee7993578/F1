package com.edufox.schoolerp.master.work.work_viewprofile;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/ViewProfile.jsx. */
@RestController("page_master_work_work_viewprofile_viewprofilecontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_viewprofile")
public class ViewProfileController extends PageArtifactController<ViewProfileService> {
    public ViewProfileController(ViewProfileService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}