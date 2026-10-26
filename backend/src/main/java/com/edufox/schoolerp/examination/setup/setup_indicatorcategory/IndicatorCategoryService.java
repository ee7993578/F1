package com.edufox.schoolerp.examination.setup.setup_indicatorcategory;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/IndicatorCategory.jsx. */
@Service("page_examination_setup_setup_indicatorcategory_indicatorcategoryservice_java")
public class IndicatorCategoryService extends PageArtifactService<IndicatorCategoryRepository> {
    public IndicatorCategoryService(IndicatorCategoryRepository repository) {
        super(repository);
    }
}