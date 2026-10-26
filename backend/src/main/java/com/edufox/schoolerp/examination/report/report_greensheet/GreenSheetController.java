package com.edufox.schoolerp.examination.report.report_greensheet;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/GreenSheet.jsx. */
@RestController("page_examination_report_report_greensheet_greensheetcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_greensheet")
public class GreenSheetController extends PageArtifactController<GreenSheetService> {
    public GreenSheetController(GreenSheetService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}