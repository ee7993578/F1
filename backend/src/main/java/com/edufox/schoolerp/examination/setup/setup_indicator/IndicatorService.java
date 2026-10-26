package com.edufox.schoolerp.examination.setup.setup_indicator;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/Indicator.jsx. */
@Service("page_examination_setup_setup_indicator_indicatorservice_java")
public class IndicatorService extends PageArtifactService<IndicatorRepository> {
    public IndicatorService(IndicatorRepository repository) {
        super(repository);
    }
}