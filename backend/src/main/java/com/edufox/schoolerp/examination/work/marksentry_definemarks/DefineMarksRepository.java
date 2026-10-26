package com.edufox.schoolerp.examination.work.marksentry_definemarks;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/marksEntry/DefineMarks.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_marksentry_definemarks_definemarksrepository_java")
public class DefineMarksRepository extends PageArtifactRepository {
    public DefineMarksRepository() {
        super("ExamMaster", "work", "DefineMarks", "ExamMaster/marksEntry/DefineMarks.jsx");
    }
}