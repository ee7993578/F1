package com.edufox.schoolerp.student.report.reports_studentinfosheet_copy;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StudentInfoSheet copy.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_studentinfosheet_copy_studentinfosheet_copyrepository_java")
public class StudentInfoSheet_copyRepository extends PageArtifactRepository {
    public StudentInfoSheet_copyRepository() {
        super("Student", "report", "StudentInfoSheet copy", "Student/Reports/StudentInfoSheet copy.jsx");
    }
}