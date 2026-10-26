package com.edufox.schoolerp.student.report.reports_udisepromotionreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/UDISEPromotionReport.jsx. */
@Service("page_student_report_reports_udisepromotionreport_udisepromotionreportservice_java")
public class UDISEPromotionReportService extends PageArtifactService<UDISEPromotionReportRepository> {
    public UDISEPromotionReportService(UDISEPromotionReportRepository repository) {
        super(repository);
    }
}