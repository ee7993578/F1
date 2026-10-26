package com.edufox.schoolerp.student.work.work_searchstudent;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/SearchStudent.jsx. */
@Service("page_student_work_work_searchstudent_searchstudentservice_java")
public class SearchStudentService extends PageArtifactService<SearchStudentRepository> {
    public SearchStudentService(SearchStudentRepository repository) {
        super(repository);
    }
}