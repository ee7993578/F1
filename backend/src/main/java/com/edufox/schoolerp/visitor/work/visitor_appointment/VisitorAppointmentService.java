package com.edufox.schoolerp.visitor.work.visitor_appointment;

import com.edufox.schoolerp.common.page.PageArtifactService;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("visitor_appointment_service")
public class VisitorAppointmentService extends PageArtifactService<VisitorAppointmentRepository> {
    private final VisitorService workflow;

    public VisitorAppointmentService(VisitorAppointmentRepository repository, VisitorService workflow) {
        super(repository);
        this.workflow = workflow;
    }

    public List<Map<String, Object>> search(String schoolId, String term) { return workflow.searchVisitors(schoolId, term); }
    public Map<String, Object> appointments(String schoolId, boolean todayOnly) { return workflow.appointments(schoolId, todayOnly); }
    public Map<String, Object> appointment(String schoolId, int id) { return workflow.appointment(schoolId, id); }
    public Map<String, Object> save(String schoolId, Integer userId, Map<String, Object> body, Integer id) { return workflow.saveAppointment(schoolId, userId, body, id); }
    public Map<String, Object> cancel(String schoolId, int id, String reason) { return workflow.cancel(schoolId, id, reason); }
}
