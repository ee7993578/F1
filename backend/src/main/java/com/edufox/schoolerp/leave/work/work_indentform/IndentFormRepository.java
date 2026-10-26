package com.edufox.schoolerp.leave.work.work_indentform;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/IndentForm.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_indentform_indentformrepository_java")
public class IndentFormRepository extends PageArtifactRepository {
    public IndentFormRepository() {
        super("Leave", "work", "IndentForm", "Leave/Work/IndentForm.jsx");
    }
}