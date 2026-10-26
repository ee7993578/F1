package com.edufox.schoolerp.student.setup.setup_housemaster;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Setup/HouseMaster.jsx. */
@Repository("page_com_edufox_schoolerp_student_setup_setup_housemaster_housemasterrepository_java")
public class HouseMasterRepository extends PageArtifactRepository {
    public HouseMasterRepository() {
        super("Student", "setup", "HouseMaster", "Student/Setup/HouseMaster.jsx");
    }
}