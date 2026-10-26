package com.edufox.schoolerp.library.report.report_booklist;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Library/Report/BookList.jsx. */
@Service("page_library_report_report_booklist_booklistservice_java")
public class BookListService extends PageArtifactService<BookListRepository> {
    public BookListService(BookListRepository repository) {
        super(repository);
    }
}