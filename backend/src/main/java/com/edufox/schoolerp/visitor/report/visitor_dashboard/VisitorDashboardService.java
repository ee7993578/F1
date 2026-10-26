package com.edufox.schoolerp.visitor.report.visitor_dashboard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service("visitor_dashboard_service")
public class VisitorDashboardService extends PageArtifactService<VisitorDashboardRepository> {
    public VisitorDashboardService(VisitorDashboardRepository repository) { super(repository); }

    public Map<String, Object> dashboard(String schoolId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("stats", repository.stats(schoolId));
        result.put("trend", repository.trend(schoolId));
        result.put("inside", repository.inside(schoolId));
        return result;
    }

    public Map<String, Object> lookupAppointment(String schoolId, String reference) {
        Map<String, Object> row = repository.appointment(schoolId, reference);
        if (row == null) return Map.of("success", false, "message", "No appointment found for reference number '" + reference.trim() + "'.");
        return Map.of("success", true, "data", row);
    }
}
