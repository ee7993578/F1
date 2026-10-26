package com.edufox.schoolerp.leave.work.work_indentform;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Leave/Work/IndentForm.jsx. */
@RestController("page_leave_work_work_indentform_indentformcontroller_java")
@RequestMapping("/api/page-artifacts/leave/work/work_indentform")
public class IndentFormController extends PageArtifactController<IndentFormService> {
    public IndentFormController(IndentFormService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}