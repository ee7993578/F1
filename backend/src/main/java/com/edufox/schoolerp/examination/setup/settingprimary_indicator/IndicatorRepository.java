package com.edufox.schoolerp.examination.setup.settingprimary_indicator;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/SettingPrimary/Indicator.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_settingprimary_indicator_indicatorrepository_java")
public class IndicatorRepository extends PageArtifactRepository {
    public IndicatorRepository() {
        super("ExamMaster", "setup", "Indicator", "ExamMaster/SettingPrimary/Indicator.jsx");
    }
}