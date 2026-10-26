package com.edufox.schoolerp.visitor.report.visitor_dashboard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("visitor_dashboard_repository")
public class VisitorDashboardRepository extends PageArtifactRepository {
    private final VisitorRepository delegate;

    public VisitorDashboardRepository(VisitorRepository delegate) {
        super("Visitor", "report", "Dashboard", "VisitorModule.jsx:VisitorDashboard");
        this.delegate = delegate;
    }

    public Map<String, Object> stats(String schoolId) { return delegate.dashboardStats(schoolId); }
    public List<Map<String, Object>> trend(String schoolId) { return delegate.dashboardTrend(schoolId); }
    public List<Map<String, Object>> inside(String schoolId) { return delegate.inside(schoolId); }
    public Map<String, Object> appointment(String schoolId, String reference) { return delegate.lookupAppointment(schoolId, reference); }
}
