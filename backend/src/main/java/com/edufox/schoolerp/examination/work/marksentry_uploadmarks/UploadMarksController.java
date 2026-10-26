package com.edufox.schoolerp.examination.work.marksentry_uploadmarks;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/marksEntry/UploadMarks.jsx. */
@RestController("page_examination_work_marksentry_uploadmarks_uploadmarkscontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/marksentry_uploadmarks")
public class UploadMarksController extends PageArtifactController<UploadMarksService> {
    public UploadMarksController(UploadMarksService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}