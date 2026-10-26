package com.edufox.schoolerp.library.report.library_report;

import com.edufox.schoolerp.common.auth.AuthContext;
import com.edufox.schoolerp.library.shared.library.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Read-only Library report pages. */
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class LibraryReportController {
    private final LibraryService service;

    @GetMapping("/reports/{kind}")
    public List<Map<String, Object>> report(@PathVariable String kind, @RequestParam Map<String, String> query, Authentication a) {
        return service.report(kind, AuthContext.value(a, "schoolId"), query);
    }
}
