package com.edufox.schoolerp.ptm;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PtmService {
    private final PtmRepository repository;
    private final DatabaseWriteGuard writeGuard;

    public Map<String, Object> bootstrap(String schoolId, String staffUid, String requestedSession) {
        String session = clean(requestedSession); if (session.isBlank()) session = repository.currentSession(schoolId);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("sessions", repository.sessions(schoolId));
        out.put("currentSession", session);
        out.put("classes", repository.classesForStaff(schoolId, session, staffUid));
        out.put("meetingTypes", List.of(Map.of("value", "Physical", "label", "Physical"), Map.of("value", "Online", "label", "Online")));
        out.put("statuses", List.of(Map.of("value", "Draft", "label", "Draft"), Map.of("value", "Published", "label", "Published"), Map.of("value", "Completed", "label", "Completed")));
        out.put("masters", repository.masterList(schoolId));
        return out;
    }

    public Map<String, Object> edit(String schoolId, int id) {
        Map<String, Object> row = repository.master(schoolId, id);
        if (row == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PTM record not found.");
        return row;
    }

    @Transactional
    public Map<String, Object> save(String schoolId, String staffUid, Map<String, Object> body, Integer id) {
        writeGuard.requireWritable();
        String session = required(body, "session", "Session is required.");
        String name = required(body, "ptmName", "Please enter PTM Name.");
        String date = requiredDate(body.get("ptmDate"));
        String start = required(body, "startTime", "Please enter Start Time.");
        String end = required(body, "endTime", "Please enter End Time.");
        try {
            if (!LocalTime.parse(start).isBefore(LocalTime.parse(end))) throw bad("End Time must be greater than Start Time.");
        } catch (DateTimeParseException e) { throw bad("Invalid Start Time or End Time."); }
        String meetingType = clean(body.get("meetingType")); if (meetingType.isBlank()) meetingType = "Physical";
        String status = clean(body.get("status")); if (status.isBlank()) status = "Draft";
        String venue = clean(body.get("venue")), remarks = clean(body.get("remarks"));
        List<?> classIds = list(body.get("classIds")), sectionIds = list(body.get("sectionIds"));
        if (classIds.isEmpty()) throw bad("Please select at least one Class.");
        if (sectionIds.isEmpty()) throw bad("Please select at least one Section.");
        List<Map<String, Object>> classes = repository.classesForStaff(schoolId, session, staffUid);
        List<Map<String, Object>> sections = repository.sectionsForStaff(schoolId, session, staffUid, classIds);
        Set<Integer> allowedClasses = ids(classes, "classId"), allowedSections = ids(sections, "secId");
        if (!allAllowed(classIds, allowedClasses)) throw bad("One or more selected classes are not available for this staff account.");
        if (!allAllowed(sectionIds, allowedSections)) throw bad("One or more selected sections are not available for this staff account.");
        int ptmId;
        if (id == null || id <= 0) {
            ptmId = repository.insertMaster(schoolId, session, name, date, start, end, meetingType, venue, status, remarks, staffUid);
            repository.replaceClasses(ptmId, classIds, classes);
            repository.deleteSectionMappings(ptmId);
            repository.replaceSections(ptmId, sectionIds, sections);
            repository.insertStudentMappings(ptmId, schoolId, session);
            return Map.of("message", "PTM created successfully.", "ptmId", ptmId);
        }
        int updated = repository.updateMaster(id, schoolId, session, name, date, start, end, meetingType, venue, status, remarks);
        if (updated == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PTM record not found.");
        repository.deleteClassMappings(id);
        repository.replaceClasses(id, classIds, classes);
        repository.deleteSectionMappings(id);
        repository.replaceSections(id, sectionIds, sections);
        repository.updateStudentMappings(id, schoolId, session);
        return Map.of("message", "PTM updated successfully.", "ptmId", id);
    }

    public Map<String, Object> allReport(String schoolId, String session, String status, String search) {
        List<Map<String, Object>> rows = repository.reportRows(schoolId, clean(session), clean(status), clean(search));
        int completed=0,pending=0,absent=0,students=0;
        for (Map<String,Object> row: rows) { completed+=number(row.get("completedMeetings")); pending+=number(row.get("pendingMeetings")); absent+=number(row.get("parentAbsent")); students+=number(row.get("totalStudents")); }
        return Map.of("rows", rows, "stats", Map.of("totalPTMs", rows.size(), "totalStudents", students, "totalCompleted", completed, "totalPending", pending, "totalAbsent", absent));
    }

    public Map<String, Object> facultyReport(String schoolId, String session) {
        List<Map<String,Object>> upcomingRows = repository.upcoming(schoolId, clean(session));
        Map<String,Map<String,Object>> grouped = new LinkedHashMap<>();
        for (Map<String,Object> row: upcomingRows) {
            String key=clean(row.get("ptmName"))+"|"+clean(row.get("teacher"))+"|"+clean(row.get("subject"))+"|"+clean(row.get("date"))+"|"+clean(row.get("time"));
            Map<String,Object> item=grouped.computeIfAbsent(key,k->{Map<String,Object> x=new LinkedHashMap<>();x.put("teacher",clean(row.get("teacher")));x.put("subject",clean(row.get("subject")));x.put("cls",clean(row.get("cls")));x.put("sections",new LinkedHashSet<String>());x.put("date",clean(row.get("date")));x.put("time",clean(row.get("time")));x.put("ptmName",clean(row.get("ptmName")));return x;});
            @SuppressWarnings("unchecked") Set<String> sections=(Set<String>)item.get("sections"); String section=clean(row.get("sectionName")); if(!section.isBlank())sections.add(section);
        }
        for(Map<String,Object> row:grouped.values()){ @SuppressWarnings("unchecked") Set<String> sections=(Set<String>)row.get("sections");row.put("sections",String.join(", ",sections)); }
        return Map.of("faculty",repository.facultySummary(schoolId, clean(session)), "upcoming",new ArrayList<>(grouped.values()), "stats",repository.overview(schoolId));
    }

    public Map<String, Object> dashboard(String schoolId, String session) {
        String activeSession = clean(session);
        Map<String, Object> report = allReport(schoolId, activeSession, "", "");
        return Map.of(
            "session", activeSession,
            "stats", report.get("stats"),
            "rows", report.get("rows"),
            "upcoming", repository.upcoming(schoolId, activeSession)
        );
    }

    private static String required(Map<String,Object> body,String key,String message){String value=clean(body.get(key));if(value.isBlank())throw bad(message);return value;}
    private static String requiredDate(Object raw){String value=clean(raw);if(value.isBlank())throw bad("Please select PTM Date.");try{return LocalDate.parse(value).toString();}catch(Exception ignored){try{return LocalDate.parse(value,DateTimeFormatter.ofPattern("dd-MMM-yyyy",Locale.ENGLISH)).toString();}catch(Exception e){throw bad("Please enter a valid PTM Date.");}}}
    private static List<?> list(Object raw){return raw instanceof Collection<?> c?new ArrayList<>(c):List.of();}
    private static Set<Integer> ids(List<Map<String,Object>> rows,String key){Set<Integer> out=new HashSet<>();for(Map<String,Object> row:rows)try{out.add(Integer.parseInt(clean(row.get(key))));}catch(Exception ignored){}return out;}
    private static boolean allAllowed(List<?> values,Set<Integer> allowed){for(Object value:values)try{if(!allowed.contains(Integer.parseInt(clean(value))))return false;}catch(Exception e){return false;}return true;}
    private static int number(Object value){try{return Integer.parseInt(clean(value));}catch(Exception ignored){return 0;}}
    private static String clean(Object value){return value==null?"":String.valueOf(value).trim();}
    private static ResponseStatusException bad(String message){return new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
}
