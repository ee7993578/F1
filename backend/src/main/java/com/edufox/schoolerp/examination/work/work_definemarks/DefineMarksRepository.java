package com.edufox.schoolerp.examination.work.work_definemarks;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/DefineMarks.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_definemarks_definemarksrepository_java")
public class DefineMarksRepository extends PageArtifactRepository {
    public DefineMarksRepository() {
        super("ExamMaster", "work", "DefineMarks", "ExamMaster/Work/DefineMarks.jsx");
    }
}