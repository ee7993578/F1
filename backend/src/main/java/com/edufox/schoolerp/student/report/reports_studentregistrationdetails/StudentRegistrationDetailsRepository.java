package com.edufox.schoolerp.student.report.reports_studentregistrationdetails;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StudentRegistrationDetails.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_studentregistrationdetails_studentregistrationdetailsrepository_java")
public class StudentRegistrationDetailsRepository extends PageArtifactRepository {
    public StudentRegistrationDetailsRepository() {
        super("Student", "report", "StudentRegistrationDetails", "Student/Reports/StudentRegistrationDetails.jsx");
    }
}