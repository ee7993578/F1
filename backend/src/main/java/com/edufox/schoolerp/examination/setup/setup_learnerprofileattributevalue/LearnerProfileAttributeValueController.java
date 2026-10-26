package com.edufox.schoolerp.examination.setup.setup_learnerprofileattributevalue;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/LearnerProfileAttributeValue.jsx. */
@RestController("page_examination_setup_setup_learnerprofileattributevalue_learnerprofileattributevaluecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_learnerprofileattributevalue")
public class LearnerProfileAttributeValueController extends PageArtifactController<LearnerProfileAttributeValueService> {
    public LearnerProfileAttributeValueController(LearnerProfileAttributeValueService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}