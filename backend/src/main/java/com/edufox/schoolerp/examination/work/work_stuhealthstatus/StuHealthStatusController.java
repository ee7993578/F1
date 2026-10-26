package com.edufox.schoolerp.examination.work.work_stuhealthstatus;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/StuHealthStatus.jsx. */
@RestController("page_examination_work_work_stuhealthstatus_stuhealthstatuscontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_stuhealthstatus")
public class StuHealthStatusController extends PageArtifactController<StuHealthStatusService> {
    public StuHealthStatusController(StuHealthStatusService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}