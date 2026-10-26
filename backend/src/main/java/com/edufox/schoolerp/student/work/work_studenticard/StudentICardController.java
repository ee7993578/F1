package com.edufox.schoolerp.student.work.work_studenticard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/StudentICard.jsx. */
@RestController("page_student_work_work_studenticard_studenticardcontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_studenticard")
public class StudentICardController extends PageArtifactController<StudentICardService> {
    public StudentICardController(StudentICardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}