package com.edufox.schoolerp.staff.update.update_facultyinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Update/FacultyInfosheet.jsx. */
@Service("page_staff_update_update_facultyinfosheet_facultyinfosheetservice_java")
public class FacultyInfosheetService extends PageArtifactService<FacultyInfosheetRepository> {
    public FacultyInfosheetService(FacultyInfosheetRepository repository) {
        super(repository);
    }
}