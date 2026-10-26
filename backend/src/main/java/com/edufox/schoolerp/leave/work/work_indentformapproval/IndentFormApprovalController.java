package com.edufox.schoolerp.leave.work.work_indentformapproval;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/IndentFormApproval.jsx. */
@RestController("page_leave_work_work_indentformapproval_indentformapprovalcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_indentformapproval")
public class IndentFormApprovalController extends PageArtifactController<IndentFormApprovalService> {
    public IndentFormApprovalController(IndentFormApprovalService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}