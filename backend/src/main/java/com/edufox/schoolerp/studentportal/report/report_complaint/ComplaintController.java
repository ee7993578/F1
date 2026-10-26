package com.edufox.schoolerp.studentportal.report.report_complaint;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/Complaint.jsx. */
@RestController("page_studentportal_report_report_complaint_complaintcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_complaint")
public class ComplaintController extends PageArtifactController<ComplaintService> {
    public ComplaintController(ComplaintService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}