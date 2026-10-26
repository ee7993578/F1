package com.edufox.schoolerp.examination.setup.settingbasic_definesubsubject;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/SettingBasic/DefineSubSubject.jsx. */
@RestController("page_examination_setup_settingbasic_definesubsubject_definesubsubjectcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/settingbasic_definesubsubject")
public class DefineSubSubjectController extends PageArtifactController<DefineSubSubjectService> {
    public DefineSubSubjectController(DefineSubSubjectService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}