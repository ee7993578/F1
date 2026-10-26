package com.edufox.schoolerp.student.work.work_studenticard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/StudentICard.jsx. */
@Service("page_student_work_work_studenticard_studenticardservice_java")
public class StudentICardService extends PageArtifactService<StudentICardRepository> {
    public StudentICardService(StudentICardRepository repository) {
        super(repository);
    }
}