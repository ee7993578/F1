package com.edufox.schoolerp.examination.setup.settingprimary_definemaxminexam;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/SettingPrimary/DefineMaxMinExam.jsx. */
@RestController("page_examination_setup_settingprimary_definemaxminexam_definemaxminexamcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/settingprimary_definemaxminexam")
public class DefineMaxMinExamController extends PageArtifactController<DefineMaxMinExamService> {
    public DefineMaxMinExamController(DefineMaxMinExamService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}