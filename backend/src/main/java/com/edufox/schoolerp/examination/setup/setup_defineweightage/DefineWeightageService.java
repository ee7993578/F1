package com.edufox.schoolerp.examination.setup.setup_defineweightage;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/DefineWeightage.jsx. */
@Service("page_examination_setup_setup_defineweightage_defineweightageservice_java")
public class DefineWeightageService extends PageArtifactService<DefineWeightageRepository> {
    public DefineWeightageService(DefineWeightageRepository repository) {
        super(repository);
    }
}