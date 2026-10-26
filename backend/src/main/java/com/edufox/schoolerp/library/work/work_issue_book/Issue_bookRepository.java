package com.edufox.schoolerp.library.work.work_issue_book;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Work/Issue_book.jsx. */
@Repository("page_com_edufox_schoolerp_library_work_work_issue_book_issue_bookrepository_java")
public class Issue_bookRepository extends PageArtifactRepository {
    public Issue_bookRepository() {
        super("Library", "work", "Issue_book", "Library/Work/Issue_book.jsx");
    }
}