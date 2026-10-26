package com.edufox.schoolerp.leave.work.work_indentformlist;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/IndentFormList.jsx. */
@RestController("page_leave_work_work_indentformlist_indentformlistcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_indentformlist")
public class IndentFormListController extends PageArtifactController<IndentFormListService> {
    public IndentFormListController(IndentFormListService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}