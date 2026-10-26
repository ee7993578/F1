package com.edufox.schoolerp.website.work.work_uploadnotice;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for WebSite/Work/UploadNotice.jsx. */
@Repository("page_com_edufox_schoolerp_website_work_work_uploadnotice_uploadnoticerepository_java")
public class UploadNoticeRepository extends PageArtifactRepository {
    public UploadNoticeRepository() {
        super("WebSite", "work", "UploadNotice", "WebSite/Work/UploadNotice.jsx");
    }
}