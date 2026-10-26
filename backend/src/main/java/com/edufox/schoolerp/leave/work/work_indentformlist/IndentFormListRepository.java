package com.edufox.schoolerp.leave.work.work_indentformlist;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Leave/Work/IndentFormList.jsx. */
@Repository("page_com_edufox_schoolerp_leave_work_work_indentformlist_indentformlistrepository_java")
public class IndentFormListRepository extends PageArtifactRepository {
    public IndentFormListRepository() {
        super("Leave", "work", "IndentFormList", "Leave/Work/IndentFormList.jsx");
    }
}