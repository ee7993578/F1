package com.edufox.schoolerp.student.update.update_updatestudentinfowithdrawn;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/UpdateStudentInfoWithdrawn.jsx. */
@RestController("page_student_update_update_updatestudentinfowithdrawn_updatestudentinfowithdrawncontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_updatestudentinfowithdrawn")
public class UpdateStudentInfoWithdrawnController extends PageArtifactController<UpdateStudentInfoWithdrawnService> {
    public UpdateStudentInfoWithdrawnController(UpdateStudentInfoWithdrawnService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}