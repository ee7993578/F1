package com.edufox.schoolerp.uploads.setup.setup_defineactivity;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Setup/DefineActivity.jsx. */
@Service("page_uploads_setup_setup_defineactivity_defineactivityservice_java")
public class DefineActivityService extends PageArtifactService<DefineActivityRepository> {
    public DefineActivityService(DefineActivityRepository repository) {
        super(repository);
    }
}