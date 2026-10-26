package com.edufox.schoolerp.examination.setup.settingprimary_definecategory;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/SettingPrimary/DefineCategory.jsx. */
@Service("page_examination_setup_settingprimary_definecategory_definecategoryservice_java")
public class DefineCategoryService extends PageArtifactService<DefineCategoryRepository> {
    public DefineCategoryService(DefineCategoryRepository repository) {
        super(repository);
    }
}