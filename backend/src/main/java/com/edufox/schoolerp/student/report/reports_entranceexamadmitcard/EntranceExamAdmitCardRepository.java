package com.edufox.schoolerp.student.report.reports_entranceexamadmitcard;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Reports/EntranceExamAdmitCard.jsx. */
@Repository("page_com_edufox_schoolerp_student_report_reports_entranceexamadmitcard_entranceexamadmitcardrepository_java")
public class EntranceExamAdmitCardRepository extends PageArtifactRepository {
    public EntranceExamAdmitCardRepository() {
        super("Student", "report", "EntranceExamAdmitCard", "Student/Reports/EntranceExamAdmitCard.jsx");
    }
}