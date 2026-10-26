package com.edufox.schoolerp.examination.work.marksentry_definemarkspreprimary;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/marksEntry/DefineMarksPrePrimary.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_marksentry_definemarkspreprimary_definemarkspreprimaryrepository_java")
public class DefineMarksPrePrimaryRepository extends PageArtifactRepository {
    public DefineMarksPrePrimaryRepository() {
        super("ExamMaster", "work", "DefineMarksPrePrimary", "ExamMaster/marksEntry/DefineMarksPrePrimary.jsx");
    }
}