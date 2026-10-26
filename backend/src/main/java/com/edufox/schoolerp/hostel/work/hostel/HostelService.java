package com.edufox.schoolerp.hostel.work.hostel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HostelService {
    private final HostelRepository repository;
    public Map<String, Object> bootstrap(String school) { return repository.bootstrap(school); }
    public List<Map<String, Object>> students(String school, String session, String classId, String admissionNo) { return repository.students(school, session, classId, admissionNo); }
    public List<Map<String, Object>> assignments(String school, String session, String classId, String search) { return repository.assignments(school, session, classId, search); }
    @Transactional public Map<String, Object> saveAssignments(String school, String session, String staff, List<?> rows) { return repository.saveAssignments(school, session, staff, rows); }
    @Transactional public Map<String, Object> saveFees(String school, String session, String staff, List<?> rows) { return repository.saveFees(school, session, staff, rows); }
}
