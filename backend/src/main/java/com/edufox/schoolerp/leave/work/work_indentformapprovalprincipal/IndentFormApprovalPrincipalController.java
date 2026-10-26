package com.edufox.schoolerp.leave.work.work_indentformapprovalprincipal;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/IndentFormApprovalPrincipal.jsx. */
@RestController("page_leave_work_work_indentformapprovalprincipal_indentformapprovalprincipalcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_indentformapprovalprincipal")
public class IndentFormApprovalPrincipalController extends PageArtifactController<IndentFormApprovalPrincipalService> {
    public IndentFormApprovalPrincipalController(IndentFormApprovalPrincipalService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}