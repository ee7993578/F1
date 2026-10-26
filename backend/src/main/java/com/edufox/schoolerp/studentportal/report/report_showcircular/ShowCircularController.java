package com.edufox.schoolerp.studentportal.report.report_showcircular;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/ShowCircular.jsx. */
@RestController("page_studentportal_report_report_showcircular_showcircularcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_showcircular")
public class ShowCircularController extends PageArtifactController<ShowCircularService> {
    public ShowCircularController(ShowCircularService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}