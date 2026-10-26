package com.edufox.schoolerp.studentportal.report.report_showassignment;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/ShowAssignment.jsx. */
@Service("page_studentportal_report_report_showassignment_showassignmentservice_java")
public class ShowAssignmentService extends PageArtifactService<ShowAssignmentRepository> {
    public ShowAssignmentService(ShowAssignmentRepository repository) {
        super(repository);
    }
}