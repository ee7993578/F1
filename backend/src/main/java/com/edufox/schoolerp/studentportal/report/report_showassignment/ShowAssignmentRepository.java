package com.edufox.schoolerp.studentportal.report.report_showassignment;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/ShowAssignment.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_showassignment_showassignmentrepository_java")
public class ShowAssignmentRepository extends PageArtifactRepository {
    public ShowAssignmentRepository() {
        super("StundentPages", "report", "ShowAssignment", "StundentPages/Report/ShowAssignment.jsx");
    }
}