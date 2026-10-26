package com.edufox.schoolerp.staff.update.update_facultyinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Update/FacultyInfosheet.jsx. */
@Repository("page_com_edufox_schoolerp_staff_update_update_facultyinfosheet_facultyinfosheetrepository_java")
public class FacultyInfosheetRepository extends PageArtifactRepository {
    public FacultyInfosheetRepository() {
        super("Staff", "update", "FacultyInfosheet", "Staff/Update/FacultyInfosheet.jsx");
    }
}