package com.edufox.schoolerp.student.report.reports_studentlist;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StudentList.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_studentlist_studentlistrepository_java")
public class StudentListRepository extends PageArtifactRepository {
    public StudentListRepository() {
        super("Student", "report", "StudentList", "Student/Reports/StudentList.jsx");
    }
}