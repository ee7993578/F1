package com.edufox.schoolerp.visitor;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VisitorService {
    private final VisitorRepository repository;
    private final DatabaseWriteGuard writeGuard;

    public Map<String,Object> bootstrap(String schoolId) {
        Map<String,Object> out = new LinkedHashMap<>();
        out.put("visitorTypes", repository.visitorTypes());
        out.put("departments", repository.departments());
        out.put("gates", repository.gates());
        out.put("purposes", repository.purposes(schoolId));
        out.put("hosts", repository.hosts());
        return out;
    }

    public Map<String,Object> config(String schoolId) {
        Map<String,Object> row = repository.config(schoolId);
        if (row != null) return row;
        return Map.of("schoolId", schoolId, "walkinApprovalRequired", true,
            "walkinDirectEntryAllowed", false, "appointmentApprovalRequired", true,
            "appointmentDirectEntryAllowed", false, "configured", false);
    }

    public List<Map<String,Object>> searchVisitors(String schoolId, String term) {
        if (clean(term).length() < 2) return List.of();
        return repository.searchVisitors(schoolId, clean(term));
    }

    public Map<String,Object> appointments(String schoolId, boolean today) {
        return Map.of("rows", repository.appointments(schoolId, today), "todayOnly", today);
    }

    public Map<String,Object> appointment(String schoolId, int id) {
        Map<String,Object> row = repository.appointmentEdit(schoolId, id);
        if (row == null) throw notFound("Appointment not found.");
        return row;
    }

    @Transactional
    public Map<String,Object> saveAppointment(String schoolId, Integer userId, Map<String,Object> body, Integer id) {
        writeGuard.requireWritable();
        String name = required(body, "visitorName", "Please enter or select a visitor.");
        String mobile = required(body, "visitorMobile", "Please enter the visitor mobile number or select a visitor from Search Visitor.");
        String hostName = required(body, "hostName", "Please select a person to meet.");
        LocalDate date = date(body.get("date"), "Appointment date is required.");
        int persons = number(body.get("noOfPersons"), 1);
        if (persons < 1) throw bad("Please enter at least 1 person.");
        int duration = number(body.get("duration"), 30); if (duration == 0) duration = 30;
        LocalTime time = optionalTime(body.get("time"));
        Timestamp entry = time == null ? null : Timestamp.valueOf(LocalDateTime.of(date, time));
        Timestamp exit = entry == null ? null : Timestamp.valueOf(LocalDateTime.of(date, time).plusMinutes(duration));
        Integer department = positive(body.get("departmentId"));
        Integer purpose = positive(body.get("purposeId"));
        Integer gate = positive(body.get("gateId"));
        Integer visitorType = positive(body.get("visitorTypeId"));
        String idProofNumber = clean(body.get("idProofNumber"));
        if (repository.isBlacklisted(mobile, idProofNumber)) throw bad("This visitor is blacklisted and cannot be booked for an appointment.");
        String role = clean(body.get("hostRole")); if (role.isBlank()) role = "Faculty";
        int hostId = repository.saveHost(schoolId, hostName, department, role);
        int visitorId = repository.upsertVisitor(schoolId, positive(body.get("visitorId")), visitorType, name, mobile,
            clean(body.get("visitorEmail")), clean(body.get("gender")), optionalDate(body.get("dob")),
            clean(body.get("idProofType")), idProofNumber, clean(body.get("visitorPhoto")),
            clean(body.get("visitorAddress")), userId, clean(body.get("visitorRefId")));
        int visitId;
        if (id != null && id > 0) {
            int affected = repository.updateVisit(schoolId, id, department, purpose, hostId, gate, date, entry, exit,
                persons, clean(body.get("vehicleNo")), clean(body.get("notes")));
            if (affected == 0) throw notFound("Appointment not found.");
            visitId = id;
        } else {
            visitId = repository.insertVisit(schoolId, visitorId, department, purpose, hostId, gate, date, entry, exit,
                persons, clean(body.get("vehicleNo")), clean(body.get("notes")), true, userId);
        }
        return Map.of("message", id != null && id > 0 ? "Appointment updated successfully." : "Appointment saved successfully.", "visitId", visitId);
    }

    @Transactional
    public Map<String,Object> cancel(String schoolId, int id, String reason) {
        writeGuard.requireWritable();
        String text = clean(reason); if (text.isBlank()) throw bad("Cancellation remark is required.");
        if (repository.cancelVisit(schoolId, id, text) == 0) throw notFound("Appointment not found.");
        return Map.of("message", "Appointment cancelled.");
    }

    public Map<String,Object> approval(String schoolId, String search, Integer typeId, Integer departmentId,
                                       String visitDate, String status) {
        LocalDate date = optionalDate(visitDate);
        List<Map<String,Object>> rows = repository.approvalRows(schoolId, search, positive(typeId), positive(departmentId), date,
            clean(status).isBlank() ? "Pending" : status);
        Map<String,Object> summary = repository.approvalSummary(schoolId);
        return Map.of("rows", rows, "summary", summary, "status", clean(status).isBlank() ? "Pending" : status);
    }

    @Transactional
    public Map<String,Object> setApproval(String schoolId, Integer userId, int id, String status, String reason) {
        writeGuard.requireWritable();
        String normalized = clean(status);
        if (!normalized.equalsIgnoreCase("Approved") && !normalized.equalsIgnoreCase("Rejected")) throw bad("Invalid approval status.");
        if (normalized.equalsIgnoreCase("Rejected") && clean(reason).isBlank()) throw bad("Rejection remark is required.");
        if (repository.setApproval(schoolId, id, normalized, userId, reason) == 0) throw notFound("Visitor request not found.");
        return Map.of("message", normalized.equalsIgnoreCase("Approved") ? "Visitor request approved." : "Visitor request rejected.");
    }

    public Map<String,Object> history(String schoolId, String from, String to, String status, String search) {
        LocalDate fromDate = optionalDate(from), toDate = optionalDate(to);
        List<Map<String,Object>> base = repository.historyRows(schoolId, fromDate, toDate, search);
        List<Map<String,Object>> rows = new ArrayList<>();
        for (Map<String,Object> row : base) if (matchesStatus(row, status)) rows.add(row);
        int checkedIn=0, checkedOut=0, inside=0;
        for (Map<String,Object> row : base) {
            String visit = clean(row.get("visitStatus"));
            boolean in = !clean(row.get("checkInTime")).isBlank();
            boolean out = !clean(row.get("checkOutTime")).isBlank();
            if (visit.equalsIgnoreCase("CheckedIn") || in) checkedIn++;
            if (visit.equalsIgnoreCase("CheckedOut") || out) checkedOut++;
            if (visit.equalsIgnoreCase("CheckedIn")) inside++;
        }
        return Map.of("rows", rows, "stats", Map.of("totalVisitors", base.size(), "checkedIn", checkedIn, "checkedOut", checkedOut, "currentlyInside", inside));
    }

    public Map<String,Object> details(String schoolId, int id) {
        Map<String,Object> row = repository.visitDetails(schoolId, id);
        if (row == null) throw notFound("Visitor details could not be found.");
        return row;
    }

    public Map<String,Object> dashboard(String schoolId) {
        return Map.of("stats", repository.dashboardStats(schoolId), "trend", repository.dashboardTrend(schoolId), "inside", repository.inside(schoolId));
    }

    public Map<String,Object> lookupAppointment(String schoolId, String refNo) {
        Map<String,Object> row = repository.lookupAppointment(schoolId, clean(refNo));
        if (row == null) return Map.of("success", false, "message", "No appointment found for reference number '" + clean(refNo) + "'.");
        return Map.of("success", true, "data", row);
    }

    @Transactional
    public Map<String,Object> saveConfig(String schoolId, Integer userId, Map<String,Object> body) {
        writeGuard.requireWritable();
        boolean walkinApproval = bool(body.get("walkinApprovalRequired"), true);
        boolean appointmentApproval = bool(body.get("appointmentApprovalRequired"), true);
        repository.saveConfig(schoolId, walkinApproval, !walkinApproval, appointmentApproval, !appointmentApproval, userId);
        return Map.of("message", "Visitor approval settings saved successfully.", "config", config(schoolId));
    }

    private static boolean matchesStatus(Map<String,Object> row, String raw) {
        String filter = clean(raw).toLowerCase(Locale.ROOT); if (filter.isBlank()) return true;
        String status = clean(row.get("visitStatus"));
        boolean in = !clean(row.get("checkInTime")).isBlank();
        boolean out = !clean(row.get("checkOutTime")).isBlank();
        return switch (filter) {
            case "checkedin" -> status.equalsIgnoreCase("CheckedIn") || in;
            case "checkedout" -> status.equalsIgnoreCase("CheckedOut") || out;
            case "insidenow" -> status.equalsIgnoreCase("CheckedIn") || (in && !out);
            case "scheduled", "pending" -> status.equalsIgnoreCase("Scheduled") || status.equalsIgnoreCase("Pending");
            case "cancelled", "canceled", "rejected" -> status.equalsIgnoreCase("Cancelled") || status.equalsIgnoreCase("Canceled") || status.equalsIgnoreCase("Rejected");
            default -> status.equalsIgnoreCase(raw);
        };
    }

    private static String required(Map<String,Object> body, String key, String message) { String value=clean(body.get(key)); if (value.isBlank()) throw bad(message); return value; }
    private static int number(Object value, int fallback) { try { String x=clean(value); return x.isBlank()?fallback:Integer.parseInt(x); } catch(Exception ignored){return fallback;} }
    private static Integer positive(Object value) { try { int x=Integer.parseInt(clean(value)); return x>0?x:null; } catch(Exception ignored){return null;} }
    private static boolean bool(Object value, boolean fallback) { String x=clean(value); return x.isBlank()?fallback:Boolean.parseBoolean(x); }
    private static String clean(Object value) { return value==null?"":String.valueOf(value).trim(); }
    private static LocalDate date(Object value, String message) { LocalDate d=optionalDate(value); if(d==null) throw bad(message); return d; }
    private static LocalDate optionalDate(Object value) { String x=clean(value); if(x.isBlank()) return null; try{return LocalDate.parse(x);}catch(Exception ignored){} try{return LocalDate.parse(x,DateTimeFormatter.ofPattern("dd-MMM-yyyy",Locale.ENGLISH));}catch(Exception ignored){return null;} }
    private static LocalTime optionalTime(Object value) { String x=clean(value); if(x.isBlank()) return null; try{return LocalTime.parse(x);}catch(Exception ignored){} try{return LocalTime.parse(x,DateTimeFormatter.ofPattern("hh:mm a",Locale.ENGLISH));}catch(Exception ignored){return null;} }
    private static ResponseStatusException bad(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST,message); }
    private static ResponseStatusException notFound(String message) { return new ResponseStatusException(HttpStatus.NOT_FOUND,message); }
}
