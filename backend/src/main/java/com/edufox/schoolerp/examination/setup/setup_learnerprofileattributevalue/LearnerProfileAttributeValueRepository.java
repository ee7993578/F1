package com.edufox.schoolerp.examination.setup.setup_learnerprofileattributevalue;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/LearnerProfileAttributeValue.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_learnerprofileattributevalue_learnerprofileattributevaluerepository_java")
public class LearnerProfileAttributeValueRepository extends PageArtifactRepository {
    public LearnerProfileAttributeValueRepository() {
        super("ExamMaster", "setup", "LearnerProfileAttributeValue", "ExamMaster/Setup/LearnerProfileAttributeValue.jsx");
    }
}