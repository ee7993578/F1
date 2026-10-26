package com.edufox.schoolerp.student.update.update_updatestudentinfowithdrawn;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/UpdateStudentInfoWithdrawn.jsx. */
@Service("page_student_update_update_updatestudentinfowithdrawn_updatestudentinfowithdrawnservice_java")
public class UpdateStudentInfoWithdrawnService extends PageArtifactService<UpdateStudentInfoWithdrawnRepository> {
    public UpdateStudentInfoWithdrawnService(UpdateStudentInfoWithdrawnRepository repository) {
        super(repository);
    }
}