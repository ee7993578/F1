package com.edufox.schoolerp.staff.update.update_facultyinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Update/FacultyInfosheet.jsx. */
@RestController("page_staff_update_update_facultyinfosheet_facultyinfosheetcontroller_java")
@RequestMapping("/api/page-artifacts/staff/update/update_facultyinfosheet")
public class FacultyInfosheetController extends PageArtifactController<FacultyInfosheetService> {
    public FacultyInfosheetController(FacultyInfosheetService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}