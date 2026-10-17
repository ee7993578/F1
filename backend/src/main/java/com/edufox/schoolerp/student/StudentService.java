package com.edufox.schoolerp.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository repository;

    public Map<String, Object> bootstrap(String schoolId) { return repository.bootstrap(schoolId); }

    public List<Map<String, Object>> terms() { return repository.terms(); }

    public List<Map<String, Object>> imageUploadSections(String schoolId, String staffUid, String session) {
        if (session == null || session.length() < 4 || staffUid == null || staffUid.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session and authenticated staff are required");
        }
        return repository.imageUploadSections(schoolId, staffUid, session);
    }

    public List<Map<String, Object>> imageUploadStudents(String schoolId, String staffUid,
                                                       String session, Integer sectionId) {
        boolean allowed = sectionId != null && imageUploadSections(schoolId, staffUid, session).stream()
            .anyMatch(row -> String.valueOf(sectionId).equals(String.valueOf(row.get("sectionId"))));
        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Section is not assigned for this session");
        return repository.search(schoolId, null, null, sectionId, "Registered", session);
    }

    public List<Map<String, Object>> infosheetLookup(String schoolId, String session, boolean withdrawn,
                                                    String registrationNo, String feeBookNo) {
        boolean hasRegistration = registrationNo != null && !registrationNo.isBlank();
        boolean hasFeeBook = feeBookNo != null && !feeBookNo.isBlank();
        if (session == null || session.isBlank() || hasRegistration == hasFeeBook) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session and one exact lookup value are required");
        }
        return repository.infosheetLookup(schoolId, session, withdrawn ? "Withdrawn" : "Registered",
            hasRegistration ? registrationNo.trim() : null, hasFeeBook ? feeBookNo.trim() : null);
    }

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
