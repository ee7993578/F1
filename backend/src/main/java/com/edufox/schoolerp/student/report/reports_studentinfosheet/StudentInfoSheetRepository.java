package com.edufox.schoolerp.student.report.reports_studentinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StudentInfoSheet.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_studentinfosheet_studentinfosheetrepository_java")
public class StudentInfoSheetRepository extends PageArtifactRepository {
    public StudentInfoSheetRepository() {
        super("Student", "report", "StudentInfoSheet", "Student/Reports/StudentInfoSheet.jsx");
    }
}