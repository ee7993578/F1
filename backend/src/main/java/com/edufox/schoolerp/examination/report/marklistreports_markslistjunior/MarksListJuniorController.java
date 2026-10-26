package com.edufox.schoolerp.examination.report.marklistreports_markslistjunior;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/MarkListReports/MarksListJunior.jsx. */
@RestController("page_examination_report_marklistreports_markslistjunior_markslistjuniorcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/marklistreports_markslistjunior")
public class MarksListJuniorController extends PageArtifactController<MarksListJuniorService> {
    public MarksListJuniorController(MarksListJuniorService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}