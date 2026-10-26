package com.edufox.schoolerp.studentportal.report.report_initiatestudentpayment;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/InitiateStudentPayment.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_initiatestudentpayment_initiatestudentpaymentrepository_java")
public class InitiateStudentPaymentRepository extends PageArtifactRepository {
    public InitiateStudentPaymentRepository() {
        super("StundentPages", "report", "InitiateStudentPayment", "StundentPages/Report/InitiateStudentPayment.jsx");
    }
}