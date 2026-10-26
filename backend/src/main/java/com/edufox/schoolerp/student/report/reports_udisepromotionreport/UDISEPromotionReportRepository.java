package com.edufox.schoolerp.student.report.reports_udisepromotionreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/UDISEPromotionReport.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_udisepromotionreport_udisepromotionreportrepository_java")
public class UDISEPromotionReportRepository extends PageArtifactRepository {
    public UDISEPromotionReportRepository() {
        super("Student", "report", "UDISEPromotionReport", "Student/Reports/UDISEPromotionReport.jsx");
    }
}