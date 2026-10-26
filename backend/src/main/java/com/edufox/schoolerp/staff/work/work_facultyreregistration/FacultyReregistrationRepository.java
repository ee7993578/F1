package com.edufox.schoolerp.staff.work.work_facultyreregistration;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Work/FacultyReregistration.jsx. */
@Repository("page_com_edufox_schoolerp_staff_work_work_facultyreregistration_facultyreregistrationrepository_java")
public class FacultyReregistrationRepository extends PageArtifactRepository {
    public FacultyReregistrationRepository() {
        super("Staff", "work", "FacultyReregistration", "Staff/Work/FacultyReregistration.jsx");
    }
}