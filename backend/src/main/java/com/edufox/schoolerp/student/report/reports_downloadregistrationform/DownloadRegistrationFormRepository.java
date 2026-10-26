package com.edufox.schoolerp.student.report.reports_downloadregistrationform;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/DownloadRegistrationForm.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_downloadregistrationform_downloadregistrationformrepository_java")
public class DownloadRegistrationFormRepository extends PageArtifactRepository {
    public DownloadRegistrationFormRepository() {
        super("Student", "report", "DownloadRegistrationForm", "Student/Reports/DownloadRegistrationForm.jsx");
    }
}