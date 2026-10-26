package com.edufox.schoolerp.studentportal.report.report_showassignment;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/ShowAssignment.jsx. */
@RestController("page_studentportal_report_report_showassignment_showassignmentcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_showassignment")
public class ShowAssignmentController extends PageArtifactController<ShowAssignmentService> {
    public ShowAssignmentController(ShowAssignmentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}