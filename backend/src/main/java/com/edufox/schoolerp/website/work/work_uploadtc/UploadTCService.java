package com.edufox.schoolerp.website.work.work_uploadtc;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for WebSite/Work/UploadTC.jsx. */
@Service("page_website_work_work_uploadtc_uploadtcservice_java")
public class UploadTCService extends PageArtifactService<UploadTCRepository> {
    public UploadTCService(UploadTCRepository repository) {
        super(repository);
    }
}