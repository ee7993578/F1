package com.edufox.schoolerp.student.report.reports_studentidpassword;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StudentIDPassword.jsx. */
@Service("page_student_report_reports_studentidpassword_studentidpasswordservice_java")
public class StudentIDPasswordService extends PageArtifactService<StudentIDPasswordRepository> {
    public StudentIDPasswordService(StudentIDPasswordRepository repository) {
        super(repository);
    }
}