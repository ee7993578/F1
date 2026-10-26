package com.edufox.schoolerp.master.work.work_logreportdatewise;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for MasterSetting/Work/LogReportDateWise.jsx. */
@Service("page_master_work_work_logreportdatewise_logreportdatewiseservice_java")
public class LogReportDateWiseService extends PageArtifactService<LogReportDateWiseRepository> {
    public LogReportDateWiseService(LogReportDateWiseRepository repository) {
        super(repository);
    }
}