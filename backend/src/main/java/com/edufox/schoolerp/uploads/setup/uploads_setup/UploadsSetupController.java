package com.edufox.schoolerp.uploads.setup.uploads_setup;

import com.edufox.schoolerp.common.auth.AuthContext;
import com.edufox.schoolerp.uploads.shared.uploads.UploadsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Calendar and activity setup pages for Uploads. */
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class UploadsSetupController {
    private final UploadsService service;

    private static String school(Authentication a) { return AuthContext.value(a, "schoolId"); }
    private static String staff(Authentication a) { return AuthContext.value(a, "staffUid"); }
    private static String text(Map<String, Object> body, String key) { Object v = body.get(key); return v == null ? "" : String.valueOf(v); }

    @GetMapping("/calendar") public List<Map<String, Object>> calendar(@RequestParam String session, Authentication a) { return service.calendar(school(a), session); }
    @PostMapping("/calendar") public Map<String, Object> saveCalendar(@RequestBody Map<String, Object> body, Authentication a) { return service.saveCalendar(school(a), staff(a), text(body, "session"), text(body, "id"), text(body, "from"), text(body, "to"), text(body, "title"), "update".equalsIgnoreCase(text(body, "mode"))); }
    @DeleteMapping("/calendar/{id}") public Map<String, Object> deleteCalendar(@PathVariable String id, Authentication a) { return service.deleteCalendar(school(a), id); }
    @GetMapping("/activities") public List<Map<String, Object>> activities(@RequestParam String session, Authentication a) { return service.activities(school(a), session); }
    @PostMapping("/activities") public Map<String, Object> saveActivity(@RequestBody Map<String, Object> body, Authentication a) { return service.saveActivity(school(a), staff(a), text(body, "session"), text(body, "id"), text(body, "date"), text(body, "name"), "update".equalsIgnoreCase(text(body, "mode"))); }
}
