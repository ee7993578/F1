package com.edufox.schoolerp.student.update.update_promotedemote;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/PromoteDemote.jsx. */
@Service("page_student_update_update_promotedemote_promotedemoteservice_java")
public class PromoteDemoteService extends PageArtifactService<PromoteDemoteRepository> {
    public PromoteDemoteService(PromoteDemoteRepository repository) {
        super(repository);
    }
}