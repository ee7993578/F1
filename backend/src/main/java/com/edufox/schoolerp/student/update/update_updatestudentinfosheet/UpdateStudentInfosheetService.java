package com.edufox.schoolerp.student.update.update_updatestudentinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/UpdateStudentInfosheet.jsx. */
@Service("page_student_update_update_updatestudentinfosheet_updatestudentinfosheetservice_java")
public class UpdateStudentInfosheetService extends PageArtifactService<UpdateStudentInfosheetRepository> {
    public UpdateStudentInfosheetService(UpdateStudentInfosheetRepository repository) {
        super(repository);
    }
}