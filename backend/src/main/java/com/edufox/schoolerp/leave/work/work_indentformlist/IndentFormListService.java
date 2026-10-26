package com.edufox.schoolerp.leave.work.work_indentformlist;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Leave/Work/IndentFormList.jsx. */
@Service("page_leave_work_work_indentformlist_indentformlistservice_java")
public class IndentFormListService extends PageArtifactService<IndentFormListRepository> {
    public IndentFormListService(IndentFormListRepository repository) {
        super(repository);
    }
}