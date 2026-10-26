package com.edufox.schoolerp.studentportal.report.report_paymentsuccessful;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/PaymentSuccessful.jsx. */
@RestController("page_studentportal_report_report_paymentsuccessful_paymentsuccessfulcontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_paymentsuccessful")
public class PaymentSuccessfulController extends PageArtifactController<PaymentSuccessfulService> {
    public PaymentSuccessfulController(PaymentSuccessfulService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}