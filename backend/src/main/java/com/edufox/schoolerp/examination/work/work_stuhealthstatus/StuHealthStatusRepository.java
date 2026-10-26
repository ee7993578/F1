package com.edufox.schoolerp.examination.work.work_stuhealthstatus;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/StuHealthStatus.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_stuhealthstatus_stuhealthstatusrepository_java")
public class StuHealthStatusRepository extends PageArtifactRepository {
    public StuHealthStatusRepository() {
        super("ExamMaster", "work", "StuHealthStatus", "ExamMaster/Work/StuHealthStatus.jsx");
    }
}