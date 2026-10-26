package com.edufox.schoolerp.library.report.report_booksissuereturndetail;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Report/BooksIssueReturnDetail.jsx. */
@Service("page_library_report_report_booksissuereturndetail_booksissuereturndetailservice_java")
public class BooksIssueReturnDetailService extends PageArtifactService<BooksIssueReturnDetailRepository> {
    public BooksIssueReturnDetailService(BooksIssueReturnDetailRepository repository) {
        super(repository);
    }
}