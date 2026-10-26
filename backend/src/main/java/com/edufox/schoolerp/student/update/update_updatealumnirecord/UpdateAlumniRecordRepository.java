package com.edufox.schoolerp.student.update.update_updatealumnirecord;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/UpdateAlumniRecord.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_updatealumnirecord_updatealumnirecordrepository_java")
public class UpdateAlumniRecordRepository extends PageArtifactRepository {
    public UpdateAlumniRecordRepository() {
        super("Student", "update", "UpdateAlumniRecord", "Student/Update/UpdateAlumniRecord.jsx");
    }
}