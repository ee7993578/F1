package com.edufox.schoolerp.examination.work.work_definestudentremark;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/DefineStudentRemark.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_definestudentremark_definestudentremarkrepository_java")
public class DefineStudentRemarkRepository extends PageArtifactRepository {
    public DefineStudentRemarkRepository() {
        super("ExamMaster", "work", "DefineStudentRemark", "ExamMaster/Work/DefineStudentRemark.jsx");
    }
}