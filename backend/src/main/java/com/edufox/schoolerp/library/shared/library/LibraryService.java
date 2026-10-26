package com.edufox.schoolerp.library.shared.library;

import com.edufox.schoolerp.config.DatabaseWriteGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final LibraryRepository repo;
    private final DatabaseWriteGuard writeGuard;

    public Map<String, Object> bootstrap(String school, String session, String staff) { return repo.bootstrap(school, session, staff); }
    public List<Map<String, Object>> masters(String kind, String school, String session) { return repo.masterRows(kind, school, session); }

    @Transactional
    public Map<String, Object> saveMaster(String kind, String id, Map<String, Object> body, String school, String session, String staff) {
        writeGuard.requireWritable();
        return repo.masterSave(kind, id, body, school, session, staff, !id.isBlank());
    }

    @Transactional
    public Map<String, Object> deleteMaster(String kind, String id, String school) {
        writeGuard.requireWritable(); repo.masterDelete(kind, id, school); return message("Record deleted successfully");
    }

    public List<Map<String, Object>> publishers(String school, String session) { return repo.publishers(school, session); }
    public List<Map<String, Object>> suppliers(String school, String session) { return repo.suppliers(school, session); }

    @Transactional
    public Map<String, Object> saveContact(String type, String id, Map<String, Object> body, String school, String session, String staff) {
        writeGuard.requireWritable(); return repo.saveContact(type, id, body, school, session, staff, !id.isBlank());
    }

    @Transactional
    public Map<String, Object> deleteContact(String type, String id, String school) {
        writeGuard.requireWritable(); repo.deleteContact(type, id, school); return message("Entry deleted successfully");
    }

    public List<Map<String, Object>> items(String school, String session, String search) { return repo.items(school, session, search); }

    @Transactional
    public Map<String, Object> saveItem(String id, Map<String, Object> body, String school, String session, String staff) {
        writeGuard.requireWritable(); return repo.saveItem(id, body, school, session, staff, !id.isBlank());
    }

    @Transactional
    public Map<String, Object> deleteItem(String id, String school) {
        writeGuard.requireWritable(); repo.deleteItem(id, school); return message("Item deleted successfully");
    }

    public List<Map<String, Object>> rules(String school, String session) { return repo.rules(school, session); }
    @Transactional public Map<String, Object> saveRule(String id, Map<String, Object> body, String school, String session, String staff) { writeGuard.requireWritable(); return repo.saveRule(body, id, school, session, staff, false); }
    @Transactional public Map<String, Object> deleteRule(String id, String school) { writeGuard.requireWritable(); repo.deleteRule(id, school); return message("Rule deleted successfully"); }
    public List<Map<String, Object>> memberCategories(String school, String session) { return repo.memberCategories(school, session); }
    @Transactional public Map<String, Object> saveMemberCategory(String id, Map<String, Object> body, String school, String session, String staff) { writeGuard.requireWritable(); return repo.saveRule(body, id, school, session, staff, true); }
    @Transactional public Map<String, Object> deleteMemberCategory(String id, String school) { writeGuard.requireWritable(); repo.deleteRule(id, school); return message("Member category deleted successfully"); }

    public List<Map<String, Object>> students(String school, String session, String secId) { return repo.students(school, session, secId); }
    public List<Map<String, Object>> faculties(String school, String session) { return repo.faculties(school, session); }
    public List<Map<String, Object>> member(String type, String code, String school, String session) { return repo.memberLookup(type, code, school, session); }
    public List<Map<String, Object>> item(String accession, String school, String session) { return repo.itemLookup(accession, school, session); }
    public List<Map<String, Object>> activeIssue(String accession, String school, String session) { return repo.activeIssue(accession, school, session); }
    public List<Map<String, Object>> memberIssues(String code, String school, String session) { return repo.memberIssues(code, school, session); }

    @Transactional
    public Map<String, Object> transaction(Map<String, Object> body, String school, String session, String staff) {
        writeGuard.requireWritable();
        String operation = LibraryRepository.normalize(String.valueOf(body.getOrDefault("operation", "issue")));
        String memberType = LibraryRepository.text(body.get("memberType"));
        String accession = LibraryRepository.text(body.get("accessionNo"));
        if (accession.isBlank()) throw new IllegalArgumentException("Accession number is required");
        if ("issue".equals(operation)) {
            if (repo.itemLookup(accession, school, session).isEmpty()) throw new IllegalArgumentException("Item not found");
            if (!repo.activeIssue(accession, school, session).isEmpty()) throw new IllegalArgumentException("This item is already issued");
            Map<String,Object> item = repo.itemLookup(accession, school, session).get(0);
            if (Objects.toString(item.get("lostStatus"), "0").equals("1")) throw new IllegalArgumentException("Lost item cannot be issued");
            String category = LibraryRepository.text(item.get("categoryId"));
            repo.insertIssue(body, school, session, staff, repo.returnDays(school, memberType, category, session));
            return message("Book issued successfully");
        }
        String issueId = LibraryRepository.text(body.get("issueId"));
        if (issueId.isBlank()) issueId = accession;
        if ("return".equals(operation)) { repo.returnIssue(issueId, body, school, staff); return message("Book returned successfully"); }
        if ("renew".equals(operation)) { repo.renewIssue(issueId, body, school, session, staff, 7); return message("Book renewed successfully"); }
        if ("lost".equals(operation)) { repo.lostIssue(body, school, session, staff); return message("Book marked as lost"); }
        throw new IllegalArgumentException("Unsupported Library operation");
    }

    public List<Map<String, Object>> report(String kind, String school, Map<String, String> q) { return repo.report(kind, school, q); }
    private static Map<String,Object> message(String x) { return new LinkedHashMap<>(Map.of("message", x)); }
}
