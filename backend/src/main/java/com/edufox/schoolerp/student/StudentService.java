package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;

    public Map<String, Object> bootstrap(String schoolId) { return repository.bootstrap(schoolId); }

    public List<Map<String, Object>> search(String schoolId, String query, Integer classId,
                                            Integer sectionId, String status, String session) {
        return repository.search(schoolId, query, classId, sectionId, status, session);
    }

    public Map<String, Object> findOne(String schoolId, String studentId) {
        return repository.findOne(schoolId, studentId);
    }

    public List<Map<String, Object>> report(String schoolId, String type, String query,
                                            Integer classId, Integer sectionId, String status,
                                            String session, Integer month) {
        return repository.report(schoolId, type, query, classId, sectionId, status, session, month);
    }
}
