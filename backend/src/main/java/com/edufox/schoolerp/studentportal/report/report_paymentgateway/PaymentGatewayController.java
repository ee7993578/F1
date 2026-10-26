package com.edufox.schoolerp.studentportal.report.report_paymentgateway;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for StundentPages/Report/PaymentGateway.jsx. */
@RestController("page_studentportal_report_report_paymentgateway_paymentgatewaycontroller_java")
@RequestMapping("/api/page-artifacts/stundentpages/report/report_paymentgateway")
public class PaymentGatewayController extends PageArtifactController<PaymentGatewayService> {
    public PaymentGatewayController(PaymentGatewayService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}