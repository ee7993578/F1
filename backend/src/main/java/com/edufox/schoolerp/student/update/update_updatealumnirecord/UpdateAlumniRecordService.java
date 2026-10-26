package com.edufox.schoolerp.student.update.update_updatealumnirecord;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/UpdateAlumniRecord.jsx. */
@Service("page_student_update_update_updatealumnirecord_updatealumnirecordservice_java")
public class UpdateAlumniRecordService extends PageArtifactService<UpdateAlumniRecordRepository> {
    public UpdateAlumniRecordService(UpdateAlumniRecordRepository repository) {
        super(repository);
    }
}