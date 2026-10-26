package com.edufox.schoolerp.student.setup.setup_housemaster;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Setup/HouseMaster.jsx. */
@Service("page_student_setup_setup_housemaster_housemasterservice_java")
public class HouseMasterService extends PageArtifactService<HouseMasterRepository> {
    public HouseMasterService(HouseMasterRepository repository) {
        super(repository);
    }
}