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

    public List<Map<String, Object>> terms() { return repository.terms(); }

    public List<Map<String, Object>> search(String schoolId, String query, Integer classId,
                                            Integer sectionId, String status, String session) {
        return repository.search(schoolId, query, classId, sectionId, status, session);
    }

    public Map<String, Object> academicOptions(String schoolId, String session, Integer classId, String termId) {
        return repository.academicOptions(schoolId, session, classId, termId);
    }

    public Map<String, Object> registrationOptions(String schoolId) { return repository.registrationOptions(schoolId); }

    public Map<String, Object> admissionAllotmentOptions(String schoolId, String session, Integer classId) {
        return repository.admissionAllotmentOptions(schoolId, session, classId);
    }

    public Map<String, Object> findOne(String schoolId, String studentId) {
        return repository.findOne(schoolId, studentId);
    }

    public Map<String, Object> registrationDetail(String schoolId, long rowId) {
        return repository.registrationDetail(schoolId, rowId);
    }

    public List<Map<String, Object>> report(String schoolId, String type, String query,
                                            Integer classId, Integer sectionId, String status,
                                            String session, Integer month, String studentType,
                                            String fromDate, String toDate) {
        return repository.report(schoolId, type, query, classId, sectionId, status, session, month, studentType, fromDate, toDate);
    }
}
