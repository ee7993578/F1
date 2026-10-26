package com.edufox.schoolerp.examination.setup.setup_learnerprofileattributevalue;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/LearnerProfileAttributeValue.jsx. */
@Service("page_examination_setup_setup_learnerprofileattributevalue_learnerprofileattributevalueservice_java")
public class LearnerProfileAttributeValueService extends PageArtifactService<LearnerProfileAttributeValueRepository> {
    public LearnerProfileAttributeValueService(LearnerProfileAttributeValueRepository repository) {
        super(repository);
    }
}