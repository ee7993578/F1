package com.edufox.schoolerp.studentportal.report.report_showcircular;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/ShowCircular.jsx. */
@Service("page_studentportal_report_report_showcircular_showcircularservice_java")
public class ShowCircularService extends PageArtifactService<ShowCircularRepository> {
    public ShowCircularService(ShowCircularRepository repository) {
        super(repository);
    }
}