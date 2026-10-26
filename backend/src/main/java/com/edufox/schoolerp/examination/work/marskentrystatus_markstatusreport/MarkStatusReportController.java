package com.edufox.schoolerp.examination.work.marskentrystatus_markstatusreport;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/marskEntryStatus/MarkStatusReport.jsx. */
@RestController("page_examination_work_marskentrystatus_markstatusreport_markstatusreportcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/marskentrystatus_markstatusreport")
public class MarkStatusReportController extends PageArtifactController<MarkStatusReportService> {
    public MarkStatusReportController(MarkStatusReportService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}