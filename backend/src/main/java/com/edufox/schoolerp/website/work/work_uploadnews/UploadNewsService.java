package com.edufox.schoolerp.website.work.work_uploadnews;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for WebSite/Work/UploadNews.jsx. */
@Service("page_website_work_work_uploadnews_uploadnewsservice_java")
public class UploadNewsService extends PageArtifactService<UploadNewsRepository> {
    public UploadNewsService(UploadNewsRepository repository) {
        super(repository);
    }
}