package com.edufox.schoolerp.student.report.reports_allstudentstrength;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/AllStudentStrength.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_allstudentstrength_allstudentstrengthrepository_java")
public class AllStudentStrengthRepository extends PageArtifactRepository {
    public AllStudentStrengthRepository() {
        super("Student", "report", "AllStudentStrength", "Student/Reports/AllStudentStrength.jsx");
    }
}