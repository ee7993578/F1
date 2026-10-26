package com.edufox.schoolerp.staff.report.report_facultyidpassword;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Report/FacultyIdPassword.jsx. */
@Service("page_staff_report_report_facultyidpassword_facultyidpasswordservice_java")
public class FacultyIdPasswordService extends PageArtifactService<FacultyIdPasswordRepository> {
    public FacultyIdPasswordService(FacultyIdPasswordRepository repository) {
        super(repository);
    }
}