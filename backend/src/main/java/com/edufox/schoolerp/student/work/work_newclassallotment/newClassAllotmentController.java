package com.edufox.schoolerp.student.work.work_newclassallotment;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/newClassAllotment.jsx. */
@RestController("page_student_work_work_newclassallotment_newclassallotmentcontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_newclassallotment")
public class newClassAllotmentController extends PageArtifactController<newClassAllotmentService> {
    public newClassAllotmentController(newClassAllotmentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}