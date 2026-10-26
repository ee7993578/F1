package com.edufox.schoolerp.examination.report.report_examwisegreensheet;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/ExamwiseGreensheet.jsx. */
@RestController("page_examination_report_report_examwisegreensheet_examwisegreensheetcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_examwisegreensheet")
public class ExamwiseGreensheetController extends PageArtifactController<ExamwiseGreensheetService> {
    public ExamwiseGreensheetController(ExamwiseGreensheetService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}