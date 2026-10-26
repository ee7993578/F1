package com.edufox.schoolerp.uploads.setup.setup_defineactivity;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Uploads/Setup/DefineActivity.jsx. */
@Repository("page_com_edufox_schoolerp_uploads_setup_setup_defineactivity_defineactivityrepository_java")
public class DefineActivityRepository extends PageArtifactRepository {
    public DefineActivityRepository() {
        super("Uploads", "setup", "DefineActivity", "Uploads/Setup/DefineActivity.jsx");
    }
}