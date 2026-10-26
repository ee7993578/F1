package com.edufox.schoolerp.visitor.work.approval_settings;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("visitor_config_repository")
public class VisitorConfigRepository extends PageArtifactRepository {
    private final VisitorRepository delegate;

    public VisitorConfigRepository(VisitorRepository delegate) {
        super("Visitor", "work", "Approval Settings", "VisitorModule.jsx:VisitorConfig");
        this.delegate = delegate;
    }

    public List<Map<String, Object>> visitorTypes() { return delegate.visitorTypes(); }
    public List<Map<String, Object>> departments() { return delegate.departments(); }
    public List<Map<String, Object>> gates() { return delegate.gates(); }
    public List<Map<String, Object>> purposes(String schoolId) { return delegate.purposes(schoolId); }
    public List<Map<String, Object>> hosts() { return delegate.hosts(); }
    public Map<String, Object> config(String schoolId) { return delegate.config(schoolId); }
}
