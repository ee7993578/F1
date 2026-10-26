package com.edufox.schoolerp.student.report.reports_studentrecord;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/StudentRecord.jsx. */
@Service("page_student_report_reports_studentrecord_studentrecordservice_java")
public class StudentRecordService extends PageArtifactService<StudentRecordRepository> {
    public StudentRecordService(StudentRecordRepository repository) {
        super(repository);
    }
}