package com.edufox.schoolerp.library.report.report_booksissuereturndetail;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Report/BooksIssueReturnDetail.jsx. */
@Repository("page_com_edufox_schoolerp_library_report_report_booksissuereturndetail_booksissuereturndetailrepository_java")
public class BooksIssueReturnDetailRepository extends PageArtifactRepository {
    public BooksIssueReturnDetailRepository() {
        super("Library", "report", "BooksIssueReturnDetail", "Library/Report/BooksIssueReturnDetail.jsx");
    }
}