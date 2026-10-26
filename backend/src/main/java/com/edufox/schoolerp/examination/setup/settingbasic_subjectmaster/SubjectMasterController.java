package com.edufox.schoolerp.examination.setup.settingbasic_subjectmaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/SettingBasic/SubjectMaster.jsx. */
@RestController("page_examination_setup_settingbasic_subjectmaster_subjectmastercontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/settingbasic_subjectmaster")
public class SubjectMasterController extends PageArtifactController<SubjectMasterService> {
    public SubjectMasterController(SubjectMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}