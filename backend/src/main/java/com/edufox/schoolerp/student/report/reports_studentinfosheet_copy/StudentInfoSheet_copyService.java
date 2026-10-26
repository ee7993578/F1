package com.edufox.schoolerp.student.report.reports_studentinfosheet_copy;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StudentInfoSheet copy.jsx. */
@Service("page_student_report_reports_studentinfosheet_copy_studentinfosheet_copyservice_java")
public class StudentInfoSheet_copyService extends PageArtifactService<StudentInfoSheet_copyRepository> {
    public StudentInfoSheet_copyService(StudentInfoSheet_copyRepository repository) {
        super(repository);
    }
}