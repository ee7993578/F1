package com.edufox.schoolerp.library.work.library;

import com.edufox.schoolerp.library.shared.library.LibraryService;
import com.edufox.schoolerp.common.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Work and setup pages for Library. */
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class LibraryWorkController {
    private final LibraryService service;

    private static String school(Authentication a) { return AuthContext.value(a, "schoolId"); }
    private static String staff(Authentication a) { return AuthContext.value(a, "staffUid"); }
    private static String text(Map<String, Object> b, String key) { Object v = b.get(key); return v == null ? "" : String.valueOf(v).trim(); }
    private String effectiveSession(String requested, Authentication a) { String s = requested == null ? "" : requested.trim(); if (!s.isBlank() && !"0".equals(s)) return s; return String.valueOf(service.bootstrap(school(a), "", staff(a)).getOrDefault("session", "")); }

    @GetMapping("/bootstrap") public Map<String, Object> bootstrap(@RequestParam(defaultValue = "") String session, Authentication a) { return service.bootstrap(school(a), session, staff(a)); }
    @GetMapping("/masters/{kind}") public List<Map<String, Object>> masters(@PathVariable String kind, @RequestParam(defaultValue = "") String session, Authentication a) { return service.masters(kind, school(a), effectiveSession(session, a)); }
    @PostMapping("/masters/{kind}") public Map<String, Object> saveMaster(@PathVariable String kind, @RequestBody Map<String, Object> body, Authentication a) { return service.saveMaster(kind, text(body, "id"), body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @PutMapping("/masters/{kind}/{id}") public Map<String, Object> updateMaster(@PathVariable String kind, @PathVariable String id, @RequestBody Map<String, Object> body, Authentication a) { return service.saveMaster(kind, id, body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @DeleteMapping("/masters/{kind}/{id}") public Map<String, Object> deleteMaster(@PathVariable String kind, @PathVariable String id, Authentication a) { return service.deleteMaster(kind, id, school(a)); }

    @GetMapping("/publishers") public List<Map<String, Object>> publishers(@RequestParam(defaultValue = "") String session, Authentication a) { return service.publishers(school(a), effectiveSession(session, a)); }
    @PostMapping("/publishers") public Map<String, Object> savePublisher(@RequestBody Map<String, Object> body, Authentication a) { return service.saveContact("publisher", text(body, "id"), body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @PutMapping("/publishers/{id}") public Map<String, Object> updatePublisher(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication a) { return service.saveContact("publisher", id, body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @DeleteMapping("/publishers/{id}") public Map<String, Object> deletePublisher(@PathVariable String id, Authentication a) { return service.deleteContact("publisher", id, school(a)); }
    @GetMapping("/suppliers") public List<Map<String, Object>> suppliers(@RequestParam(defaultValue = "") String session, Authentication a) { return service.suppliers(school(a), effectiveSession(session, a)); }
    @PostMapping("/suppliers") public Map<String, Object> saveSupplier(@RequestBody Map<String, Object> body, Authentication a) { return service.saveContact("supplier", text(body, "id"), body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @PutMapping("/suppliers/{id}") public Map<String, Object> updateSupplier(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication a) { return service.saveContact("supplier", id, body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @DeleteMapping("/suppliers/{id}") public Map<String, Object> deleteSupplier(@PathVariable String id, Authentication a) { return service.deleteContact("supplier", id, school(a)); }

    @GetMapping("/items") public List<Map<String, Object>> items(@RequestParam(defaultValue = "") String session, @RequestParam(defaultValue = "") String search, Authentication a) { return service.items(school(a), effectiveSession(session, a), search); }
    @PostMapping("/items") public Map<String, Object> saveItem(@RequestBody Map<String, Object> body, Authentication a) { return service.saveItem(text(body, "id"), body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @PutMapping("/items/{id}") public Map<String, Object> updateItem(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication a) { return service.saveItem(id, body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @DeleteMapping("/items/{id}") public Map<String, Object> deleteItem(@PathVariable String id, Authentication a) { return service.deleteItem(id, school(a)); }

    @GetMapping("/rules") public List<Map<String, Object>> rules(@RequestParam(defaultValue = "") String session, Authentication a) { return service.rules(school(a), effectiveSession(session, a)); }
    @PostMapping("/rules") public Map<String, Object> saveRule(@RequestBody Map<String, Object> body, Authentication a) { return service.saveRule(text(body, "id"), body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @PutMapping("/rules/{id}") public Map<String, Object> updateRule(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication a) { return service.saveRule(id, body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @DeleteMapping("/rules/{id}") public Map<String, Object> deleteRule(@PathVariable String id, Authentication a) { return service.deleteRule(id, school(a)); }
    @GetMapping("/member-categories") public List<Map<String, Object>> memberCategories(@RequestParam(defaultValue = "") String session, Authentication a) { return service.memberCategories(school(a), effectiveSession(session, a)); }
    @PostMapping("/member-categories") public Map<String, Object> saveMemberCategory(@RequestBody Map<String, Object> body, Authentication a) { return service.saveMemberCategory(text(body, "id"), body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @PutMapping("/member-categories/{id}") public Map<String, Object> updateMemberCategory(@PathVariable String id, @RequestBody Map<String, Object> body, Authentication a) { return service.saveMemberCategory(id, body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
    @DeleteMapping("/member-categories/{id}") public Map<String, Object> deleteMemberCategory(@PathVariable String id, Authentication a) { return service.deleteMemberCategory(id, school(a)); }

    @GetMapping("/students") public List<Map<String, Object>> students(@RequestParam String session, @RequestParam(defaultValue = "") String secId, Authentication a) { return service.students(school(a), session, secId); }
    @GetMapping("/faculties") public List<Map<String, Object>> faculties(@RequestParam(defaultValue = "") String session, Authentication a) { return service.faculties(school(a), effectiveSession(session, a)); }
    @GetMapping("/member") public List<Map<String, Object>> member(@RequestParam String type, @RequestParam String code, @RequestParam String session, Authentication a) { return service.member(type, code, school(a), session); }
    @GetMapping("/item") public List<Map<String, Object>> item(@RequestParam String accessionNo, @RequestParam String session, Authentication a) { return service.item(accessionNo, school(a), session); }
    @GetMapping("/active-issue") public List<Map<String, Object>> activeIssue(@RequestParam String accessionNo, @RequestParam String session, Authentication a) { return service.activeIssue(accessionNo, school(a), session); }
    @GetMapping("/member-issues") public List<Map<String, Object>> memberIssues(@RequestParam String code, @RequestParam String session, Authentication a) { return service.memberIssues(code, school(a), session); }
    @PostMapping("/transactions") public Map<String, Object> transaction(@RequestBody Map<String, Object> body, Authentication a) { return service.transaction(body, school(a), effectiveSession(text(body, "session"), a), staff(a)); }
}
