package com.edufox.schoolerp.examination.report.greensheet_greensheetsubjectwise;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Greensheet/GreenSheetSubjectWise.jsx. */
@RestController("page_examination_report_greensheet_greensheetsubjectwise_greensheetsubjectwisecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/greensheet_greensheetsubjectwise")
public class GreenSheetSubjectWiseController extends PageArtifactController<GreenSheetSubjectWiseService> {
    public GreenSheetSubjectWiseController(GreenSheetSubjectWiseService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}