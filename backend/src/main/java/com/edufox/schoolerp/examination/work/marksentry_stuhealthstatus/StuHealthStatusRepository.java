package com.edufox.schoolerp.examination.work.marksentry_stuhealthstatus;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/marksEntry/StuHealthStatus.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_marksentry_stuhealthstatus_stuhealthstatusrepository_java")
public class StuHealthStatusRepository extends PageArtifactRepository {
    public StuHealthStatusRepository() {
        super("ExamMaster", "work", "StuHealthStatus", "ExamMaster/marksEntry/StuHealthStatus.jsx");
    }
}