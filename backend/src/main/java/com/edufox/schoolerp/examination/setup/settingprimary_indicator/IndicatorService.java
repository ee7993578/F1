package com.edufox.schoolerp.examination.setup.settingprimary_indicator;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingPrimary/Indicator.jsx. */
@Service("page_examination_setup_settingprimary_indicator_indicatorservice_java")
public class IndicatorService extends PageArtifactService<IndicatorRepository> {
    public IndicatorService(IndicatorRepository repository) {
        super(repository);
    }
}