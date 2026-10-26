package com.edufox.schoolerp.student.report.reports_studentlist;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StudentList.jsx. */
@Service("page_student_report_reports_studentlist_studentlistservice_java")
public class StudentListService extends PageArtifactService<StudentListRepository> {
    public StudentListService(StudentListRepository repository) {
        super(repository);
    }
}