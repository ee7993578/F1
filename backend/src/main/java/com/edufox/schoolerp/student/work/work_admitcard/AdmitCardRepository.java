package com.edufox.schoolerp.student.work.work_admitcard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Work/AdmitCard.jsx. */
@Repository("page_com_edufox_schoolerp_student_work_work_admitcard_admitcardrepository_java")
public class AdmitCardRepository extends PageArtifactRepository {
    public AdmitCardRepository() {
        super("Student", "work", "AdmitCard", "Student/Work/AdmitCard.jsx");
    }
}