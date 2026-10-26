package com.edufox.schoolerp.examination.report.marklistreports_markslistblank;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/MarkListReports/MarksListBlank.jsx. */
@RestController("page_examination_report_marklistreports_markslistblank_markslistblankcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/marklistreports_markslistblank")
public class MarksListBlankController extends PageArtifactController<MarksListBlankService> {
    public MarksListBlankController(MarksListBlankService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}