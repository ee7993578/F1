package com.edufox.schoolerp.student.update.update_changeadmno;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/ChangeAdmNo.jsx. */
@Service("page_student_update_update_changeadmno_changeadmnoservice_java")
public class ChangeAdmNoService extends PageArtifactService<ChangeAdmNoRepository> {
    public ChangeAdmNoService(ChangeAdmNoRepository repository) {
        super(repository);
    }
}