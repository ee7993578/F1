package com.edufox.schoolerp.studentportal.report.report_reportcardnew;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/ReportCardNew.jsx. */
@Service("page_studentportal_report_report_reportcardnew_reportcardnewservice_java")
public class ReportCardNewService extends PageArtifactService<ReportCardNewRepository> {
    public ReportCardNewService(ReportCardNewRepository repository) {
        super(repository);
    }
}