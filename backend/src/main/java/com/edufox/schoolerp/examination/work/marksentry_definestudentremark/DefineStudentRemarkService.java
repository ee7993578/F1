package com.edufox.schoolerp.examination.work.marksentry_definestudentremark;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marksEntry/DefineStudentRemark.jsx. */
@Service("page_examination_work_marksentry_definestudentremark_definestudentremarkservice_java")
public class DefineStudentRemarkService extends PageArtifactService<DefineStudentRemarkRepository> {
    public DefineStudentRemarkService(DefineStudentRemarkRepository repository) {
        super(repository);
    }
}