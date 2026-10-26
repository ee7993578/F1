package com.edufox.schoolerp.examination.report.greensheet_examcomparativesheet;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Greensheet/ExamComparativeSheet.jsx. */
@RestController("page_examination_report_greensheet_examcomparativesheet_examcomparativesheetcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/greensheet_examcomparativesheet")
public class ExamComparativeSheetController extends PageArtifactController<ExamComparativeSheetService> {
    public ExamComparativeSheetController(ExamComparativeSheetService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}