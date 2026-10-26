package com.edufox.schoolerp.student.work.work_searchstudent;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/SearchStudent.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_searchstudent_searchstudentrepository_java")
public class SearchStudentRepository extends PageArtifactRepository {
    public SearchStudentRepository() {
        super("Student", "work", "SearchStudent", "Student/Work/SearchStudent.jsx");
    }
}