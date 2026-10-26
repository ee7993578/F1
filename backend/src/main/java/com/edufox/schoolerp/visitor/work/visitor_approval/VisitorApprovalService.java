package com.edufox.schoolerp.visitor.work.visitor_approval;

import com.edufox.schoolerp.common.page.PageArtifactService;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("visitor_approval_service")
public class VisitorApprovalService extends PageArtifactService<VisitorApprovalRepository> {
    private final VisitorService workflow;

    public VisitorApprovalService(VisitorApprovalRepository repository, VisitorService workflow) {
        super(repository);
        this.workflow = workflow;
    }

    public Map<String, Object> approval(String schoolId, String search, Integer typeId, Integer departmentId, String visitDate, String status) {
        return workflow.approval(schoolId, search, typeId, departmentId, visitDate, status);
    }

    public Map<String, Object> setApproval(String schoolId, Integer userId, int id, String status, String reason) {
        return workflow.setApproval(schoolId, userId, id, status, reason);
    }
}
