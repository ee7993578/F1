package com.edufox.schoolerp.examination.work.work_preprimarymarksmapping;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/PreprimaryMarksMapping.jsx. */
@Service("page_examination_work_work_preprimarymarksmapping_preprimarymarksmappingservice_java")
public class PreprimaryMarksMappingService extends PageArtifactService<PreprimaryMarksMappingRepository> {
    public PreprimaryMarksMappingService(PreprimaryMarksMappingRepository repository) {
        super(repository);
    }
}