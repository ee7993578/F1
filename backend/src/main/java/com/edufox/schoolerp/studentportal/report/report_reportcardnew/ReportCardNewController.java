package com.edufox.schoolerp.studentportal.report.report_reportcardnew;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/ReportCardNew.jsx. */
@RestController("page_studentportal_report_report_reportcardnew_reportcardnewcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_reportcardnew")
public class ReportCardNewController extends PageArtifactController<ReportCardNewService> {
    public ReportCardNewController(ReportCardNewService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}