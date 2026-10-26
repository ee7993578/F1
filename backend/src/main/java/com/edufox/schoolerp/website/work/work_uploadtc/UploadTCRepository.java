package com.edufox.schoolerp.website.work.work_uploadtc;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for WebSite/Work/UploadTC.jsx. */
@Repository("page_com_edufox_schoolerp_website_work_work_uploadtc_uploadtcrepository_java")
public class UploadTCRepository extends PageArtifactRepository {
    public UploadTCRepository() {
        super("WebSite", "work", "UploadTC", "WebSite/Work/UploadTC.jsx");
    }
}