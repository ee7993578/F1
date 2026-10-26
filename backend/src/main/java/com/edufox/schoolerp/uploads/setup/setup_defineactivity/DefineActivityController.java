package com.edufox.schoolerp.uploads.setup.setup_defineactivity;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Setup/DefineActivity.jsx. */
@RestController("page_uploads_setup_setup_defineactivity_defineactivitycontroller_java")
@RequestMapping("/api/page-artifacts/uploads/setup/setup_defineactivity")
public class DefineActivityController extends PageArtifactController<DefineActivityService> {
    public DefineActivityController(DefineActivityService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}