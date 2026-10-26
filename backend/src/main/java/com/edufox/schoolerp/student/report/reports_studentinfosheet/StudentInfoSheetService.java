package com.edufox.schoolerp.student.report.reports_studentinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StudentInfoSheet.jsx. */
@Service("page_student_report_reports_studentinfosheet_studentinfosheetservice_java")
public class StudentInfoSheetService extends PageArtifactService<StudentInfoSheetRepository> {
    public StudentInfoSheetService(StudentInfoSheetRepository repository) {
        super(repository);
    }
}