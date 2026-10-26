package com.edufox.schoolerp.examination.setup.settingprimary_learnerprofileattributevalue;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/SettingPrimary/LearnerProfileAttributeValue.jsx. */
@RestController("page_examination_setup_settingprimary_learnerprofileattributevalue_learnerprofileattributevaluecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/settingprimary_learnerprofileattributevalue")
public class LearnerProfileAttributeValueController extends PageArtifactController<LearnerProfileAttributeValueService> {
    public LearnerProfileAttributeValueController(LearnerProfileAttributeValueService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}