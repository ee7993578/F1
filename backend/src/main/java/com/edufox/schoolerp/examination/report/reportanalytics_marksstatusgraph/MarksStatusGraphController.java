package com.edufox.schoolerp.examination.report.reportanalytics_marksstatusgraph;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/ReportAnalytics/MarksStatusGraph.jsx. */
@RestController("page_examination_report_reportanalytics_marksstatusgraph_marksstatusgraphcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/reportanalytics_marksstatusgraph")
public class MarksStatusGraphController extends PageArtifactController<MarksStatusGraphService> {
    public MarksStatusGraphController(MarksStatusGraphService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}