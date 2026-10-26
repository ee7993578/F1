package com.edufox.schoolerp.student.work.work_admnoallotment;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/AdmNoAllotment.jsx. */
@RestController("page_student_work_work_admnoallotment_admnoallotmentcontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_admnoallotment")
public class AdmNoAllotmentController extends PageArtifactController<AdmNoAllotmentService> {
    public AdmNoAllotmentController(AdmNoAllotmentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}