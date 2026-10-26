package com.edufox.schoolerp.library.report.report_booksissuereturndetail;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Report/BooksIssueReturnDetail.jsx. */
@RestController("page_library_report_report_booksissuereturndetail_booksissuereturndetailcontroller_java")
@RequestMapping("/api/page-artifacts/library/report/report_booksissuereturndetail")
public class BooksIssueReturnDetailController extends PageArtifactController<BooksIssueReturnDetailService> {
    public BooksIssueReturnDetailController(BooksIssueReturnDetailService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}