package com.edufox.schoolerp.student.work.work_studenticard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/StudentICard.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_studenticard_studenticardrepository_java")
public class StudentICardRepository extends PageArtifactRepository {
    public StudentICardRepository() {
        super("Student", "work", "StudentICard", "Student/Work/StudentICard.jsx");
    }
}