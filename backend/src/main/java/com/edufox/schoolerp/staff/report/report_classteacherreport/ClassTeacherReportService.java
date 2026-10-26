package com.edufox.schoolerp.staff.report.report_classteacherreport;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Report/ClassTeacherReport.jsx. */
@Service("page_staff_report_report_classteacherreport_classteacherreportservice_java")
public class ClassTeacherReportService extends PageArtifactService<ClassTeacherReportRepository> {
    public ClassTeacherReportService(ClassTeacherReportRepository repository) {
        super(repository);
    }
}