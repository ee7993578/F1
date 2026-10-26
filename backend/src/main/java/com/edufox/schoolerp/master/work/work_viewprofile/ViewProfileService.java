package com.edufox.schoolerp.master.work.work_viewprofile;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/ViewProfile.jsx. */
@Service("page_master_work_work_viewprofile_viewprofileservice_java")
public class ViewProfileService extends PageArtifactService<ViewProfileRepository> {
    public ViewProfileService(ViewProfileRepository repository) {
        super(repository);
    }
}