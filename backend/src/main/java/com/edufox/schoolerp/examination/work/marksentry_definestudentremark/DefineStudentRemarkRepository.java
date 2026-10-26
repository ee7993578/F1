package com.edufox.schoolerp.examination.work.marksentry_definestudentremark;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/marksEntry/DefineStudentRemark.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_marksentry_definestudentremark_definestudentremarkrepository_java")
public class DefineStudentRemarkRepository extends PageArtifactRepository {
    public DefineStudentRemarkRepository() {
        super("ExamMaster", "work", "DefineStudentRemark", "ExamMaster/marksEntry/DefineStudentRemark.jsx");
    }
}