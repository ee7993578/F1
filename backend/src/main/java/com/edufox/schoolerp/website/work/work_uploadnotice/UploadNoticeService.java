package com.edufox.schoolerp.website.work.work_uploadnotice;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for WebSite/Work/UploadNotice.jsx. */
@Service("page_website_work_work_uploadnotice_uploadnoticeservice_java")
public class UploadNoticeService extends PageArtifactService<UploadNoticeRepository> {
    public UploadNoticeService(UploadNoticeRepository repository) {
        super(repository);
    }
}