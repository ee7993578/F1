package com.edufox.schoolerp.master.work.work_viewprofile;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/ViewProfile.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_viewprofile_viewprofilerepository_java")
public class ViewProfileRepository extends PageArtifactRepository {
    public ViewProfileRepository() {
        super("MasterSetting", "work", "ViewProfile", "MasterSetting/Work/ViewProfile.jsx");
    }
}