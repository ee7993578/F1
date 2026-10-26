package com.edufox.schoolerp.master.work.work_logreportdatewise;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for MasterSetting/Work/LogReportDateWise.jsx. */
@RestController("page_master_work_work_logreportdatewise_logreportdatewisecontroller_java")
@RequestMapping("/api/page-artifacts/mastersetting/work/work_logreportdatewise")
public class LogReportDateWiseController extends PageArtifactController<LogReportDateWiseService> {
    public LogReportDateWiseController(LogReportDateWiseService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}