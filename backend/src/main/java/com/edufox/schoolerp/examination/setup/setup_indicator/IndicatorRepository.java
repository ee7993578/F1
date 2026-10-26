package com.edufox.schoolerp.examination.setup.setup_indicator;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Setup/Indicator.jsx. */
@Repository("page_com_edufox_schoolerp_examination_setup_setup_indicator_indicatorrepository_java")
public class IndicatorRepository extends PageArtifactRepository {
    public IndicatorRepository() {
        super("ExamMaster", "setup", "Indicator", "ExamMaster/Setup/Indicator.jsx");
    }
}