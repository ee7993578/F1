package com.edufox.schoolerp.examination.setup.settingsecurity_lockunlockstudent;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/SettingSecurity/LockUnlockStudent.jsx. */
@RestController("page_examination_setup_settingsecurity_lockunlockstudent_lockunlockstudentcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/settingsecurity_lockunlockstudent")
public class LockUnlockStudentController extends PageArtifactController<LockUnlockStudentService> {
    public LockUnlockStudentController(LockUnlockStudentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}