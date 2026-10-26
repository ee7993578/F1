package com.edufox.schoolerp.examination.report.report_greensheetnew;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/GreenSheetNew.jsx. */
@RestController("page_examination_report_report_greensheetnew_greensheetnewcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_greensheetnew")
public class GreenSheetNewController extends PageArtifactController<GreenSheetNewService> {
    public GreenSheetNewController(GreenSheetNewService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}