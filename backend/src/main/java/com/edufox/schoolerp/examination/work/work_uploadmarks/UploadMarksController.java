package com.edufox.schoolerp.examination.work.work_uploadmarks;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/UploadMarks.jsx. */
@RestController("page_examination_work_work_uploadmarks_uploadmarkscontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_uploadmarks")
public class UploadMarksController extends PageArtifactController<UploadMarksService> {
    public UploadMarksController(UploadMarksService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}