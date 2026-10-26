package com.edufox.schoolerp.examination.report.report_greensheetsubjectwise;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/GreenSheetSubjectWise.jsx. */
@RestController("page_examination_report_report_greensheetsubjectwise_greensheetsubjectwisecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_greensheetsubjectwise")
public class GreenSheetSubjectWiseController extends PageArtifactController<GreenSheetSubjectWiseService> {
    public GreenSheetSubjectWiseController(GreenSheetSubjectWiseService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}