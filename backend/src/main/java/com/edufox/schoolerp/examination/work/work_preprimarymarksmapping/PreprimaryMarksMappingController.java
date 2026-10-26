package com.edufox.schoolerp.examination.work.work_preprimarymarksmapping;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/PreprimaryMarksMapping.jsx. */
@RestController("page_examination_work_work_preprimarymarksmapping_preprimarymarksmappingcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_preprimarymarksmapping")
public class PreprimaryMarksMappingController extends PageArtifactController<PreprimaryMarksMappingService> {
    public PreprimaryMarksMappingController(PreprimaryMarksMappingService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}