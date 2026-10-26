package com.edufox.schoolerp.studentportal.report.report_complaint;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/Complaint.jsx. */
@Service("page_studentportal_report_report_complaint_complaintservice_java")
public class ComplaintService extends PageArtifactService<ComplaintRepository> {
    public ComplaintService(ComplaintRepository repository) {
        super(repository);
    }
}