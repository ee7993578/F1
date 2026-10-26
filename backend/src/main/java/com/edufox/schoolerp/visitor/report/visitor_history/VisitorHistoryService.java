package com.edufox.schoolerp.visitor.report.visitor_history;

import com.edufox.schoolerp.common.page.PageArtifactService;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("visitor_history_service")
public class VisitorHistoryService extends PageArtifactService<VisitorHistoryRepository> {
    private final VisitorService workflow;

    public VisitorHistoryService(VisitorHistoryRepository repository, VisitorService workflow) {
        super(repository);
        this.workflow = workflow;
    }

    public Map<String, Object> history(String schoolId, String from, String to, String status, String search) {
        return workflow.history(schoolId, from, to, status, search);
    }

    public Map<String, Object> details(String schoolId, int id) { return workflow.details(schoolId, id); }
}
