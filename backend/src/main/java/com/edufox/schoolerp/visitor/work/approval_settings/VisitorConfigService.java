package com.edufox.schoolerp.visitor.work.approval_settings;

import com.edufox.schoolerp.common.page.PageArtifactService;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service("visitor_config_service")
public class VisitorConfigService extends PageArtifactService<VisitorConfigRepository> {
    private final VisitorService workflow;

    public VisitorConfigService(VisitorConfigRepository repository, VisitorService workflow) {
        super(repository);
        this.workflow = workflow;
    }

    public Map<String, Object> bootstrap(String schoolId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("visitorTypes", repository.visitorTypes());
        result.put("departments", repository.departments());
        result.put("gates", repository.gates());
        result.put("purposes", repository.purposes(schoolId));
        result.put("hosts", repository.hosts());
        return result;
    }

    public Map<String, Object> config(String schoolId) { return workflow.config(schoolId); }
    public Map<String, Object> saveConfig(String schoolId, Integer userId, Map<String, Object> body) {
        return workflow.saveConfig(schoolId, userId, body);
    }
}
