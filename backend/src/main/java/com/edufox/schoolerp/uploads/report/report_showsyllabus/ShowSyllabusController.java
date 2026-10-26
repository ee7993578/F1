package com.edufox.schoolerp.uploads.report.report_showsyllabus;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Report/ShowSyllabus.jsx. */
@RestController("page_uploads_report_report_showsyllabus_showsyllabuscontroller_java")
@RequestMapping("/api/page-artifacts/uploads/report/report_showsyllabus")
public class ShowSyllabusController extends PageArtifactController<ShowSyllabusService> {
    public ShowSyllabusController(ShowSyllabusService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}