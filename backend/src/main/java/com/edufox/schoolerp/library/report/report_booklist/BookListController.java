package com.edufox.schoolerp.library.report.report_booklist;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Report/BookList.jsx. */
@RestController("page_library_report_report_booklist_booklistcontroller_java")
@RequestMapping("/api/page-artifacts/library/report/report_booklist")
public class BookListController extends PageArtifactController<BookListService> {
    public BookListController(BookListService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}