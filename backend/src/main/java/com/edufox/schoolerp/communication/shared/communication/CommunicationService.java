package com.edufox.schoolerp.communication.shared.communication;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CommunicationService {
    private static final Pattern MOBILE = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern JSON_RESPONSE = Pattern.compile("\\\"response(?:Code|)\\\"\\s*:\\s*\\\"?([^,\\\"}]+)", Pattern.CASE_INSENSITIVE);
    private final CommunicationRepository repository;
    private final DatabaseWriteGuard writeGuard;

    public Map<String, Object> bootstrap(String school, String requestedSession) {
        String session = requestedSession == null || requestedSession.isBlank() ? repository.defaultSession(school) : requestedSession.trim();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("session", session);
        out.put("sessions", repository.sessions(school));
        out.put("classes", repository.classes(school));
        out.put("faculty", repository.faculty(school));
        out.put("templates", repository.templates(school, false));
        out.put("feeTemplates", repository.templates(school, true));
        out.put("smsStatuses", repository.smsStatuses(school));
        out.put("emailStatuses", repository.emailStatuses(school));
        out.put("emailTypes", repository.emailTypes(school));
        out.put("provider", repository.provider(school));
        out.put("feeTypes", List.of(
            option("-1", "Select"), option("0", "Regular"),
            option("6", "Transport"), option("7", "Hostel Fee")
        ));
        out.put("defaulterTypes", List.of(
            option("1", "Partial"), option("0", "Full"), option("2", "All")
        ));
        return out;
    }

    public List<Map<String, Object>> students(String school, String session, String classIds, String studentType) {
        return repository.students(school, session, integers(classIds), studentType);
    }

    public List<Map<String, Object>> faculty(String school) { return repository.faculty(school); }

    public List<Map<String, Object>> feeStudents(String school, String session, int feeType, int installment, int defaulterType, String classId) {
        return repository.feeStudents(school, session, feeType, installment, defaulterType, classId);
    }

    @Transactional
    public Map<String, Object> sendGeneral(String school, String staff, Map<String, Object> body) {
        writeGuard.requireWritable();
        String session = text(body.get("session"));
        String type = text(body.get("type"));
        String templateId = text(body.get("templateId"));
        String message = text(body.get("message"));
        if (message.isBlank() || templateId.isBlank()) throw new IllegalArgumentException("Please select and complete your SMS text");
        if (!Set.of("1", "3", "4").contains(type)) throw new IllegalArgumentException("Please select type");
        List<Recipient> recipients = new ArrayList<>();
        if ("1".equals(type)) {
            String mobile = text(body.get("individualMobile"));
            if (!MOBILE.matcher(mobile).matches()) throw new IllegalArgumentException("Please Enter Number.");
            recipients.add(new Recipient("0", "I", mobile));
        } else if ("3".equals(type)) {
            List<String> ids = strings(body.get("studentIds"));
            for (Map<String, Object> row : repository.studentContacts(school, session, ids)) addContacts(recipients, text(row.get("userId")), "S", text(row.get("mobile")), text(row.get("phone")));
            if (recipients.isEmpty()) throw new IllegalArgumentException("Please select student");
        } else {
            List<String> ids = strings(body.get("facultyIds"));
            for (Map<String, Object> row : repository.facultyContacts(school, ids)) addContacts(recipients, text(row.get("userId")), "F", text(row.get("mobile")), text(row.get("phone")));
            if (recipients.isEmpty()) throw new IllegalArgumentException("Please select faculty");
        }
        return persistAndSend(school, staff, templateId, message, recipients);
    }

    @Transactional
    public Map<String, Object> sendFeeSms(String school, String staff, Map<String, Object> body) {
        writeGuard.requireWritable();
        String session = text(body.get("session"));
        String templateId = text(body.get("templateId"));
        String message = text(body.get("message"));
        if (message.isBlank() || templateId.isBlank()) throw new IllegalArgumentException("Please check your SMS text");
        List<String> ids = strings(body.get("studentIds"));
        List<Recipient> recipients = new ArrayList<>();
        for (Map<String, Object> row : repository.studentContacts(school, session, ids)) addContacts(recipients, text(row.get("userId")), "S", text(row.get("mobile")), text(row.get("phone")));
        if (recipients.isEmpty()) throw new IllegalArgumentException("please select atleast one student to send sms");
        return persistAndSend(school, staff, templateId, message, recipients);
    }

    private Map<String, Object> persistAndSend(String school, String staff, String templateId, String message, List<Recipient> recipients) {
        int sentRows = 0;
        for (Recipient recipient : recipients) {
            String guid = UUID.randomUUID().toString();
            repository.insertSms(school, staff, guid, templateId, recipient.userId, recipient.type, recipient.mobile, message);
            ProviderResult provider = sendToProvider(school, templateId, recipient.mobile, message);
            repository.updateSms(school, guid, provider.smsId, provider.response, provider.responseCode);
            sentRows++;
        }
        return row("message", sentRows + " Message Sent", "recipientCount", recipients.size(), "smsCount", smsParts(message), "creditCount", recipients.size() * smsParts(message));
    }

    public List<Map<String, Object>> emailReport(String school, Map<String, String> query) {
        return repository.emailReport(school, text(query.get("session")), text(query.get("entityType")), text(query.get("status")), date(query.get("from")), date(query.get("to")));
    }

    public List<Map<String, Object>> smsReport(String school, Map<String, String> query) {
        return repository.smsReport(school, text(query.get("session")), date(query.get("from")), date(query.get("to")), text(query.get("status")), text(query.get("userType")), text(query.get("mobile")), text(query.get("classId")), text(query.get("secId")));
    }

    public List<Map<String, Object>> feeNotice(String school, Map<String, String> query) {
        return repository.feeNotice(school, text(query.get("session")), integer(query.get("feeType")), integer(query.get("installment")), text(query.get("classId")), text(query.get("admissionNo")), decimal(query.get("minimumBalance")), text(query.get("orderBy")), "1".equals(text(query.get("includeLateFee"))));
    }

    @Transactional
    public Map<String, Object> queueFeeNoticeEmails(String school, String staff, Map<String, Object> body) {
        writeGuard.requireWritable();
        String session = text(body.get("session"));
        String installment = text(body.get("installment"));
        String subject = text(body.get("subject"));
        if (subject.isBlank()) throw new IllegalArgumentException("Please enter Email Subject");
        List<String> admissionNos = strings(body.get("admissionNos"));
        int count = repository.queueFeeNoticeEmails(school, staff, session, installment, subject, text(body.get("content")), admissionNos);
        return row("message", count == 0 ? "Email ID does not exist for any student." : "Email queued for " + count + " students", "count", count);
    }

    private ProviderResult sendToProvider(String school, String templateId, String mobile, String message) {
        Map<String, Object> provider = repository.provider(school);
        String url = text(provider.get("apiContent"));
        try {
            if (!url.isBlank()) {
                String mobileKey = text(provider.get("apiMobile"));
                String messageKey = text(provider.get("apiMessage"));
                if (!mobileKey.isBlank()) url += (url.contains("?") ? "&" : "?") + enc(mobileKey) + "=" + enc(mobile);
                if (!messageKey.isBlank()) url += "&" + enc(messageKey) + "=" + enc(message);
                HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
                HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(5)).build().send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body() == null ? "" : response.body();
                String code = response.statusCode() >= 200 && response.statusCode() < 300 ? "200" : String.valueOf(response.statusCode());
                Matcher matcher = JSON_RESPONSE.matcher(body); String value = matcher.find() ? matcher.group(1).trim() : body;
                return new ProviderResult("0", value, code);
            }
        } catch (Exception e) {
            return new ProviderResult("0", e.getMessage() == null ? "Message Sending Fail" : e.getMessage(), "ERROR");
        }
        return new ProviderResult("Not Configured", "SMS provider is not configured", "NOT_CONFIGURED");
    }

    private static void addContacts(List<Recipient> recipients, String userId, String type, String... phones) {
        Set<String> seen = new LinkedHashSet<>();
        for (String phone : phones) if (MOBILE.matcher(phone).matches() && seen.add(phone)) recipients.add(new Recipient(userId, type, phone));
    }

    private static int smsParts(String message) { return Math.max(1, (message == null ? 0 : message.length() + 159) / 160); }
    private static String enc(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
    private static LocalDate date(String value) {
        if (value == null || value.isBlank()) return null;
        for (DateTimeFormatter format : List.of(DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH), DateTimeFormatter.ofPattern("dd-MM-yyyy"))) {
            try { return LocalDate.parse(value.trim(), format); } catch (DateTimeParseException ignored) { }
        }
        return null;
    }
    private static List<Integer> integers(String csv) { List<Integer> result = new ArrayList<>(); for (String value : text(csv).split(",")) try { if (!value.isBlank()) result.add(Integer.parseInt(value.trim())); } catch (NumberFormatException ignored) { } return result; }
    private static int integer(String value) { try { return Integer.parseInt(text(value)); } catch (Exception e) { return 0; } }
    private static double decimal(String value) { try { return Double.parseDouble(text(value)); } catch (Exception e) { return 0d; } }
    private static List<String> strings(Object value) { if (value instanceof Collection<?> c) return c.stream().map(CommunicationService::text).filter(v -> !v.isBlank()).toList(); return Arrays.stream(text(value).split(",")).map(String::trim).filter(v -> !v.isBlank()).toList(); }
    private static String text(Object value) { return value == null ? "" : String.valueOf(value).trim(); }
    private static Map<String, Object> option(String value, String label) { return Map.of("value", value, "label", label); }
    private static Map<String, Object> row(Object... values) { Map<String, Object> out = new LinkedHashMap<>(); for (int i=0; i+1<values.length; i+=2) out.put(String.valueOf(values[i]), values[i+1]); return out; }
    private record Recipient(String userId, String type, String mobile) { }
    private record ProviderResult(String smsId, String response, String responseCode) { }
}
