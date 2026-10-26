package com.edufox.schoolerp.examination.work.work_preprimarymarksmapping;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/PreprimaryMarksMapping.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_preprimarymarksmapping_preprimarymarksmappingrepository_java")
public class PreprimaryMarksMappingRepository extends PageArtifactRepository {
    public PreprimaryMarksMappingRepository() {
        super("ExamMaster", "work", "PreprimaryMarksMapping", "ExamMaster/Work/PreprimaryMarksMapping.jsx");
    }
}