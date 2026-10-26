package com.edufox.schoolerp.studentportal.report.report_initiatestudentpayment;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/InitiateStudentPayment.jsx. */
@RestController("page_studentportal_report_report_initiatestudentpayment_initiatestudentpaymentcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_initiatestudentpayment")
public class InitiateStudentPaymentController extends PageArtifactController<InitiateStudentPaymentService> {
    public InitiateStudentPaymentController(InitiateStudentPaymentService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}