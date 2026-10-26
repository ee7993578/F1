package com.edufox.schoolerp.uploads.report.report_dailyhomeworkreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Uploads/Report/DailyHomeworkReport.jsx. */
@Service("page_uploads_report_report_dailyhomeworkreport_dailyhomeworkreportservice_java")
public class DailyHomeworkReportService extends PageArtifactService<DailyHomeworkReportRepository> {
    public DailyHomeworkReportService(DailyHomeworkReportRepository repository) {
        super(repository);
    }
}