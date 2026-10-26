package com.edufox.schoolerp.student.report.reports_studentidpassword;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StudentIDPassword.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_studentidpassword_studentidpasswordrepository_java")
public class StudentIDPasswordRepository extends PageArtifactRepository {
    public StudentIDPasswordRepository() {
        super("Student", "report", "StudentIDPassword", "Student/Reports/StudentIDPassword.jsx");
    }
}