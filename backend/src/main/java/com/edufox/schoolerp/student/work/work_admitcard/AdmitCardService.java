package com.edufox.schoolerp.student.work.work_admitcard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Work/AdmitCard.jsx. */
@Service("page_student_work_work_admitcard_admitcardservice_java")
public class AdmitCardService extends PageArtifactService<AdmitCardRepository> {
    public AdmitCardService(AdmitCardRepository repository) {
        super(repository);
    }
}