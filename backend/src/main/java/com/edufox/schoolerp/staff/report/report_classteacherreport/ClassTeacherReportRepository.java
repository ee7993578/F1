package com.edufox.schoolerp.staff.report.report_classteacherreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Report/ClassTeacherReport.jsx. */
@Repository("page_com_edufox_schoolerp_staff_report_report_classteacherreport_classteacherreportrepository_java")
public class ClassTeacherReportRepository extends PageArtifactRepository {
    public ClassTeacherReportRepository() {
        super("Staff", "report", "ClassTeacherReport", "Staff/Report/ClassTeacherReport.jsx");
    }
}