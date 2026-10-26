package com.edufox.schoolerp.student.report.reports_studentinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/StudentInfoSheet.jsx. */
@RestController("page_student_report_reports_studentinfosheet_studentinfosheetcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_studentinfosheet")
public class StudentInfoSheetController extends PageArtifactController<StudentInfoSheetService> {
    public StudentInfoSheetController(StudentInfoSheetService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}