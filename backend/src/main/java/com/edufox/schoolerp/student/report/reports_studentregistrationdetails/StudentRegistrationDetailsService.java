package com.edufox.schoolerp.student.report.reports_studentregistrationdetails;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StudentRegistrationDetails.jsx. */
@Service("page_student_report_reports_studentregistrationdetails_studentregistrationdetailsservice_java")
public class StudentRegistrationDetailsService extends PageArtifactService<StudentRegistrationDetailsRepository> {
    public StudentRegistrationDetailsService(StudentRegistrationDetailsRepository repository) {
        super(repository);
    }
}