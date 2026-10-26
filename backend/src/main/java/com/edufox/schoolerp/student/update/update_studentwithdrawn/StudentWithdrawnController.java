package com.edufox.schoolerp.student.update.update_studentwithdrawn;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/StudentWithdrawn.jsx. */
@RestController("page_student_update_update_studentwithdrawn_studentwithdrawncontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_studentwithdrawn")
public class StudentWithdrawnController extends PageArtifactController<StudentWithdrawnService> {
    public StudentWithdrawnController(StudentWithdrawnService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}