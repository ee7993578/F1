package com.edufox.schoolerp.library.work.work_issue_book;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Work/Issue_book.jsx. */
@Service("page_library_work_work_issue_book_issue_bookservice_java")
public class Issue_bookService extends PageArtifactService<Issue_bookRepository> {
    public Issue_bookService(Issue_bookRepository repository) {
        super(repository);
    }
}