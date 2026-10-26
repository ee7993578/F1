package com.edufox.schoolerp.library.work.work_issue_book;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Work/Issue_book.jsx. */
@RestController("page_library_work_work_issue_book_issue_bookcontroller_java")
@RequestMapping("/api/page-artifacts/library/work/work_issue_book")
public class Issue_bookController extends PageArtifactController<Issue_bookService> {
    public Issue_bookController(Issue_bookService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}