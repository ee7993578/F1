package com.edufox.schoolerp.examination.setup.setup_definemaxmin;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Setup/DefineMaxMin.jsx. */
@Service("page_examination_setup_setup_definemaxmin_definemaxminservice_java")
public class DefineMaxMinService extends PageArtifactService<DefineMaxMinRepository> {
    public DefineMaxMinService(DefineMaxMinRepository repository) {
        super(repository);
    }
}