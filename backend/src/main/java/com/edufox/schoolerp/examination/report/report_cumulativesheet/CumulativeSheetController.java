package com.edufox.schoolerp.examination.report.report_cumulativesheet;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/CumulativeSheet.jsx. */
@RestController("page_examination_report_report_cumulativesheet_cumulativesheetcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_cumulativesheet")
public class CumulativeSheetController extends PageArtifactController<CumulativeSheetService> {
    public CumulativeSheetController(CumulativeSheetService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}