package com.edufox.schoolerp.student.report.reports_studentrecord;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/StudentRecord.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_studentrecord_studentrecordrepository_java")
public class StudentRecordRepository extends PageArtifactRepository {
    public StudentRecordRepository() {
        super("Student", "report", "StudentRecord", "Student/Reports/StudentRecord.jsx");
    }
}