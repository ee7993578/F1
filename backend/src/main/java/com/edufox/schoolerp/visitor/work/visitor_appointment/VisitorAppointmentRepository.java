package com.edufox.schoolerp.visitor.work.visitor_appointment;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import com.edufox.schoolerp.visitor.shared.visitor.VisitorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("visitor_appointment_repository")
public class VisitorAppointmentRepository extends PageArtifactRepository {
    private final VisitorRepository delegate;

    public VisitorAppointmentRepository(VisitorRepository delegate) {
        super("Visitor", "work", "Visitor Appointment", "VisitorModule.jsx:VisitorAppointment");
        this.delegate = delegate;
    }

    public List<Map<String, Object>> search(String schoolId, String term) { return delegate.searchVisitors(schoolId, term); }
    public List<Map<String, Object>> appointments(String schoolId, boolean todayOnly) { return delegate.appointments(schoolId, todayOnly); }
    public Map<String, Object> edit(String schoolId, int id) { return delegate.appointmentEdit(schoolId, id); }
}
