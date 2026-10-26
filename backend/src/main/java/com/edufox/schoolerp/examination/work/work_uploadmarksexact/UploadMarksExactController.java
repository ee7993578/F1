package com.edufox.schoolerp.examination.work.work_uploadmarksexact;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/UploadMarksExact.jsx. */
@RestController("page_examination_work_work_uploadmarksexact_uploadmarksexactcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_uploadmarksexact")
public class UploadMarksExactController extends PageArtifactController<UploadMarksExactService> {
    public UploadMarksExactController(UploadMarksExactService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}