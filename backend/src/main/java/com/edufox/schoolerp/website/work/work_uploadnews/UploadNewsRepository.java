package com.edufox.schoolerp.website.work.work_uploadnews;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for WebSite/Work/UploadNews.jsx. */
@Repository("page_com_edufox_schoolerp_website_work_work_uploadnews_uploadnewsrepository_java")
public class UploadNewsRepository extends PageArtifactRepository {
    public UploadNewsRepository() {
        super("WebSite", "work", "UploadNews", "WebSite/Work/UploadNews.jsx");
    }
}