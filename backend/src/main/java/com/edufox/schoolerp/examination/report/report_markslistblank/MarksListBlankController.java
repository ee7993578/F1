package com.edufox.schoolerp.examination.report.report_markslistblank;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/MarksListBlank.jsx. */
@RestController("page_examination_report_report_markslistblank_markslistblankcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_markslistblank")
public class MarksListBlankController extends PageArtifactController<MarksListBlankService> {
    public MarksListBlankController(MarksListBlankService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}