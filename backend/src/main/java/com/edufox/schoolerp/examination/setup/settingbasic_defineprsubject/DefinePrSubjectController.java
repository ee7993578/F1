package com.edufox.schoolerp.examination.setup.settingbasic_defineprsubject;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/SettingBasic/DefinePrSubject.jsx. */
@RestController("page_examination_setup_settingbasic_defineprsubject_defineprsubjectcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/settingbasic_defineprsubject")
public class DefinePrSubjectController extends PageArtifactController<DefinePrSubjectService> {
    public DefinePrSubjectController(DefinePrSubjectService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}