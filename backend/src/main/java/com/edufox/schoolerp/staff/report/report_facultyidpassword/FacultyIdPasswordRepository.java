package com.edufox.schoolerp.staff.report.report_facultyidpassword;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Report/FacultyIdPassword.jsx. */
@Repository("page_com_edufox_schoolerp_staff_report_report_facultyidpassword_facultyidpasswordrepository_java")
public class FacultyIdPasswordRepository extends PageArtifactRepository {
    public FacultyIdPasswordRepository() {
        super("Staff", "report", "FacultyIdPassword", "Staff/Report/FacultyIdPassword.jsx");
    }
}