package com.edufox.schoolerp.student.update.update_changeadmno;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/ChangeAdmNo.jsx. */
@RestController("page_student_update_update_changeadmno_changeadmnocontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_changeadmno")
public class ChangeAdmNoController extends PageArtifactController<ChangeAdmNoService> {
    public ChangeAdmNoController(ChangeAdmNoService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}