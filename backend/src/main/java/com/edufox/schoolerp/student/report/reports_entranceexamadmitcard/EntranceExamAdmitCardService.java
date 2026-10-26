package com.edufox.schoolerp.student.report.reports_entranceexamadmitcard;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/EntranceExamAdmitCard.jsx. */
@Service("page_student_report_reports_entranceexamadmitcard_entranceexamadmitcardservice_java")
public class EntranceExamAdmitCardService extends PageArtifactService<EntranceExamAdmitCardRepository> {
    public EntranceExamAdmitCardService(EntranceExamAdmitCardRepository repository) {
        super(repository);
    }
}