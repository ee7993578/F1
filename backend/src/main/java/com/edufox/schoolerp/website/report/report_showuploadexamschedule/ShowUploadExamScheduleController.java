package com.edufox.schoolerp.website.report.report_showuploadexamschedule;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for WebSite/Report/ShowUploadExamSchedule.jsx. */
@RestController("page_website_report_report_showuploadexamschedule_showuploadexamschedulecontroller_java")
@RequestMapping("/api/page-artifacts/website/report/report_showuploadexamschedule")
public class ShowUploadExamScheduleController extends PageArtifactController<ShowUploadExamScheduleService> {
    public ShowUploadExamScheduleController(ShowUploadExamScheduleService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}