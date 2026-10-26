package com.edufox.schoolerp.examination.report.greensheet_greensheetprimary;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Greensheet/GreenSheetPrimary.jsx. */
@RestController("page_examination_report_greensheet_greensheetprimary_greensheetprimarycontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/greensheet_greensheetprimary")
public class GreenSheetPrimaryController extends PageArtifactController<GreenSheetPrimaryService> {
    public GreenSheetPrimaryController(GreenSheetPrimaryService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}