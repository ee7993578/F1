package com.edufox.schoolerp.library.report.report_booklist;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Library/Report/BookList.jsx. */
@Repository("page_com_edufox_schoolerp_library_report_report_booklist_booklistrepository_java")
public class BookListRepository extends PageArtifactRepository {
    public BookListRepository() {
        super("Library", "report", "BookList", "Library/Report/BookList.jsx");
    }
}