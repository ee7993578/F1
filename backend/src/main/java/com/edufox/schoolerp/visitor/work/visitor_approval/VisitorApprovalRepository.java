package com.edufox.schoolerp.visitor.work.visitor_approval;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository("visitor_approval_repository")
public class VisitorApprovalRepository extends PageArtifactRepository {
    private final VisitorRepository delegate;

    public VisitorApprovalRepository(VisitorRepository delegate) {
        super("Visitor", "work", "Visitor Approval", "VisitorModule.jsx:VisitorApproval");
        this.delegate = delegate;
    }

    public List<Map<String, Object>> rows(String schoolId, String search, Integer typeId, Integer departmentId, LocalDate date, String status) {
        return delegate.approvalRows(schoolId, search, typeId, departmentId, date, status);
    }

    public Map<String, Object> summary(String schoolId) { return delegate.approvalSummary(schoolId); }
}
