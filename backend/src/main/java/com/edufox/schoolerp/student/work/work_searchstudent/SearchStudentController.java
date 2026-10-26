package com.edufox.schoolerp.student.work.work_searchstudent;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/SearchStudent.jsx. */
@RestController("page_student_work_work_searchstudent_searchstudentcontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_searchstudent")
public class SearchStudentController extends PageArtifactController<SearchStudentService> {
    public SearchStudentController(SearchStudentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}