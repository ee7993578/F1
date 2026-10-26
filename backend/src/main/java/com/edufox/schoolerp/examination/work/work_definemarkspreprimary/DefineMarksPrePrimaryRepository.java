package com.edufox.schoolerp.examination.work.work_definemarkspreprimary;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/DefineMarksPrePrimary.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_definemarkspreprimary_definemarkspreprimaryrepository_java")
public class DefineMarksPrePrimaryRepository extends PageArtifactRepository {
    public DefineMarksPrePrimaryRepository() {
        super("ExamMaster", "work", "DefineMarksPrePrimary", "ExamMaster/Work/DefineMarksPrePrimary.jsx");
    }
}