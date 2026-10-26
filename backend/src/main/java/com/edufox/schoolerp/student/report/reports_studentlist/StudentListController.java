package com.edufox.schoolerp.student.report.reports_studentlist;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/StudentList.jsx. */
@RestController("page_student_report_reports_studentlist_studentlistcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_studentlist")
public class StudentListController extends PageArtifactController<StudentListService> {
    public StudentListController(StudentListService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}