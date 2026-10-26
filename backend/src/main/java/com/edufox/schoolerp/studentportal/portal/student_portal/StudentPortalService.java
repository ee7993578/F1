package com.edufox.schoolerp.studentportal.portal.student_portal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentPortalService {
    private final StudentPortalRepository repository;

    public String session(String school, String requested) { return repository.currentSession(school, requested); }

    public StudentPortalRepository.StudentContext context(String school, String session, String staffUid, String subject, String role, String studentId) {
        return repository.resolve(school, session, staffUid, subject, role, studentId);
    }

    public List<Map<String, Object>> students(String school, String session, String staffUid, String subject, String role) {
        return repository.studentsForLogin(school, repository.currentSession(school, session), staffUid, subject, role);
    }

    public Map<String, Object> bootstrap(String school, String staffUid, String subject, String role, String requestedSession, String requestedStudent) {
        String session = repository.currentSession(school, requestedSession);
        var context = repository.resolve(school, session, staffUid, subject, role, requestedStudent);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("session", session);
        result.put("student", repository.profile(school, context));
        result.put("students", repository.studentsForLogin(school, session, staffUid, subject, role));
        result.put("syllabus", repository.syllabus(school, context));
        result.put("circulars", repository.circulars(school, session));
        result.put("holidayHomework", repository.holidayHomework(school, context));
        result.put("dailyHomework", repository.dailyHomework(school, context, ""));
        result.put("assignments", repository.assignments(school, context));
        result.put("fees", repository.feeRows(school, context));
        result.put("transactions", repository.transactions(school, context));
        result.put("attendance", repository.attendance(school, context));
        result.put("timetable", repository.timetable(school, context));
        result.put("examSchedule", repository.examSchedule(school, context));
        result.put("academicPerformance", repository.academicPerformance(school, context, ""));
        result.put("events", repository.events(school, context));
        return result;
    }

    public Map<String, Object> page(String school, StudentPortalRepository.StudentContext c, String page) {
        return switch (page) {
            case "syllabus" -> Map.of("rows", repository.syllabus(school, c));
            case "circulars" -> Map.of("rows", repository.circulars(school, c.session()));
            case "holiday-homework" -> Map.of("rows", repository.holidayHomework(school, c));
            case "assignments" -> Map.of("rows", repository.assignments(school, c));
            case "fees" -> Map.of("student", repository.profile(school, c), "rows", repository.feeRows(school, c), "transactions", repository.transactions(school, c));
            case "attendance" -> Map.of("rows", repository.attendance(school, c));
            case "timetable" -> Map.of("rows", repository.timetable(school, c));
            case "exam-schedule" -> Map.of("rows", repository.examSchedule(school, c));
            case "academic-performance" -> Map.of("rows", repository.academicPerformance(school, c, ""));
            default -> Map.of();
        };
    }

    public List<Map<String, Object>> dailyHomework(String school, StudentPortalRepository.StudentContext c, String date) { return repository.dailyHomework(school, c, date); }
    public List<Map<String, Object>> attendanceDetail(String school, StudentPortalRepository.StudentContext c, String month) { return repository.attendanceDetail(school, c, month); }
    public List<Map<String, Object>> academicPerformance(String school, StudentPortalRepository.StudentContext c, String term) { return repository.academicPerformance(school, c, term); }
    public Map<String, Object> paymentUrl(String school, StudentPortalRepository.StudentContext c, String installmentNo) { return repository.paymentUrl(school, c, installmentNo); }
}
