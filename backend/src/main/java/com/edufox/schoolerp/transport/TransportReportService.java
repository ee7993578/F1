package com.edufox.schoolerp.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransportReportService {
    private final TransportReportRepository repository;
    public Map<String,Object> bootstrap(String school, String session) { return repository.bootstrap(school, session); }
    public List<Map<String,Object>> assignedRoute(String school, String session, String cls, String route) { return repository.assignedRoute(school, session, cls, route); }
    public List<Map<String,Object>> student(String school, String session, String cls, String route, String slab) { return repository.student(school, session, cls, route, slab); }
    public List<Map<String,Object>> privateStudents(String school, String session, String cls) { return repository.privateStudents(school, session, cls); }
    public Map<String,Object> monthwise(String school, String session, String cls, String month, String route, String status, String receive) { return repository.monthwise(school, session, cls, month, route, status, receive); }
    public List<Map<String,Object>> routeCollection(String school, String session, String route, String installment, String cls, String category) { return repository.routeCollection(school, session, route, installment, cls, category); }
    public List<Map<String,Object>> routeCollectionSummary(String school, String session, String installment) { return repository.routeCollectionSummary(school, session, installment); }
}
