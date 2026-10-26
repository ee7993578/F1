package com.edufox.schoolerp.examination.setup.settingprimary_learnerprofileattributevalue;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingPrimary/LearnerProfileAttributeValue.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingprimary_learnerprofileattributevalue_learnerprofileattributevaluerepository_java")
public class LearnerProfileAttributeValueRepository extends PageArtifactRepository {
    public LearnerProfileAttributeValueRepository() {
        super("ExamMaster", "setup", "LearnerProfileAttributeValue", "ExamMaster/SettingPrimary/LearnerProfileAttributeValue.jsx");
    }
}