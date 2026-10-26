package com.edufox.schoolerp.student.update.update_updatestudentinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/UpdateStudentInfosheet.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_updatestudentinfosheet_updatestudentinfosheetrepository_java")
public class UpdateStudentInfosheetRepository extends PageArtifactRepository {
    public UpdateStudentInfosheetRepository() {
        super("Student", "update", "UpdateStudentInfosheet", "Student/Update/UpdateStudentInfosheet.jsx");
    }
}