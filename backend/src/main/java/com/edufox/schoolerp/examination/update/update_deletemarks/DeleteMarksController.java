package com.edufox.schoolerp.examination.update.update_deletemarks;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Update/DeleteMarks.jsx. */
@RestController("page_examination_update_update_deletemarks_deletemarkscontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/update/update_deletemarks")
public class DeleteMarksController extends PageArtifactController<DeleteMarksService> {
    public DeleteMarksController(DeleteMarksService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}