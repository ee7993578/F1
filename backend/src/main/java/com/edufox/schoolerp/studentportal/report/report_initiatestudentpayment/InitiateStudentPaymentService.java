package com.edufox.schoolerp.studentportal.report.report_initiatestudentpayment;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/InitiateStudentPayment.jsx. */
@Service("page_studentportal_report_report_initiatestudentpayment_initiatestudentpaymentservice_java")
public class InitiateStudentPaymentService extends PageArtifactService<InitiateStudentPaymentRepository> {
    public InitiateStudentPaymentService(InitiateStudentPaymentRepository repository) {
        super(repository);
    }
}