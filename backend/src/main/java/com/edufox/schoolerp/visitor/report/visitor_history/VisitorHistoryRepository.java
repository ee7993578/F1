package com.edufox.schoolerp.visitor.report.visitor_history;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository("visitor_history_repository")
public class VisitorHistoryRepository extends PageArtifactRepository {
    private final VisitorRepository delegate;

    public VisitorHistoryRepository(VisitorRepository delegate) {
        super("Visitor", "report", "Visitor Report", "VisitorModule.jsx:VisitorHistory");
        this.delegate = delegate;
    }

    public List<Map<String, Object>> rows(String schoolId, LocalDate from, LocalDate to, String search) {
        return delegate.historyRows(schoolId, from, to, search);
    }

    public Map<String, Object> details(String schoolId, int id) { return delegate.visitDetails(schoolId, id); }
}
