package com.edufox.schoolerp.studentportal.report.report_complaint;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/Complaint.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_complaint_complaintrepository_java")
public class ComplaintRepository extends PageArtifactRepository {
    public ComplaintRepository() {
        super("StundentPages", "report", "Complaint", "StundentPages/Report/Complaint.jsx");
    }
}